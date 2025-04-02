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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import Model.User;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/User")
public class UserServlet extends HttpServlet implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");


// TODO: Implement `decodeJWT` method to extract email from JWT
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> jsonResponse = new HashMap<>();

    // Extract the Authorization header
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.put("success", false);
        jsonResponse.put("message", "Missing or invalid Authorization header");
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
        return;
    }

    // Extract the token from the header
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
        // Query the user by email
        User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        // Prepare user details excluding password
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
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        jsonResponse.put("success", false);
        jsonResponse.put("message", "Error retrieving user details");
        out.print(objectMapper.writeValueAsString(jsonResponse));
        out.flush();
    } finally {
        em.close();
    }
}
    private String decodeJWT(String token) {
        return "mock-jwt-token".equals(token) ? "test@example.com" : null;
    }
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

    private boolean registerUser(String name, String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(password);
            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    private boolean authenticateUser(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return user != null && BCrypt.checkpw(password, user.getPassword());
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    private String generateJWT(String email) {
        return "mock-jwt-token";
    }

}
