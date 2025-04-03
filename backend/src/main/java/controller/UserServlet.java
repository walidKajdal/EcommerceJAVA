package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.io.Serializable;
import java.util.*;
import java.security.Key;
import Model.User;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/User")
public class UserServlet extends HttpServlet implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private static final String SECRET_KEY = "your-secret-key-change-this-1234567890123456"; // 32 chars

    //  Decode JWT Token and Extract Email
    private String decodeJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // Extracts email
        } catch (JwtException e) {
            return null;
        }
    }

    //  Generate JWT Token
    private String generateJWT(String email) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //  GET: Retrieve User Info
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonResponse = new HashMap<>();

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Missing or invalid Authorization header");
            out.print(objectMapper.writeValueAsString(jsonResponse));
            out.flush();
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String email = decodeJWT(token);

        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid token");
            out.print(objectMapper.writeValueAsString(jsonResponse));
            out.flush();
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();

            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("name", user.getUsername());
            userDetails.put("email", user.getEmail());

            jsonResponse.put("success", true);
            jsonResponse.put("user", userDetails);
            out.print(objectMapper.writeValueAsString(jsonResponse));
            out.flush();
        } catch (NoResultException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "User not found");
            out.print(objectMapper.writeValueAsString(jsonResponse));
            out.flush();
        } finally {
            em.close();
        }
    }

    //  POST: Register/Login
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> requestBody = objectMapper.readValue(request.getReader(), Map.class);

        String action = requestBody.get("action");
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        String name = requestBody.get("name");

        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        if ("register".equals(action)) {
            if (registerUser(name, email, password)) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "User registered successfully");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Registration failed");
            }
        } else if ("login".equals(action)) {
            if (authenticateUser(email, password)) {
                String token = generateJWT(email);
                jsonResponse.put("success", true);
                jsonResponse.put("token", token);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid email or password");
            }
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid action");
        }

        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    }

    //  Register User with Hashed Password
    private boolean registerUser(String name, String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Check if user already exists
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (count > 0) {
                return false; // User already exists
            }

            // Hash the password before saving
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            em.persist(user);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    //  Authenticate User with Hashed Password Check
    private boolean authenticateUser(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return user != null && BCrypt.checkpw(password, user.getPassword());
        } catch (NoResultException e) {
            return false;
        } finally {
            em.close();
        }
    }
}
