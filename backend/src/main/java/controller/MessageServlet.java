package controller;

import Model.Message;
import Model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/api/orders/messages/*")
public class MessageServlet extends HttpServlet {
    private EntityManagerFactory emf;

    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("ecommercePU");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        EntityManager em = emf.createEntityManager();

        try {
            String orderNumber = extractOrderNumber(request);
            System.out.println("Requête POST reçue pour la commande: " + orderNumber); // DEBUG

            JSONObject json = parseRequestJson(request);
            System.out.println("Contenu du message reçu: " + json.toString()); // DEBUG

            em.getTransaction().begin();
            Order order = getOrderByNumber(em, orderNumber);
            Message message = createMessage(json, order);

            persistMessage(em, message);
            em.getTransaction().commit();

            sendSuccessResponse(response, message);
        } catch (Exception e) {
            System.err.println(" Erreur backend: " + e.getMessage()); // DEBUG
            rollbackTransaction(em);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            closeEntityManager(em);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        EntityManager em = emf.createEntityManager();

        try {
            String orderNumber = extractOrderNumber(request);
            List<Message> messages = getMessagesForOrder(em, orderNumber);
            sendMessagesResponse(response, messages);
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            closeEntityManager(em);
        }
    }

    private String extractOrderNumber(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Missing order number");
        }
        System.out.println("Order ID extracted: " + parts[1]);
        return parts[1];
    }

    // parsing JSON m3a debug
    private JSONObject parseRequestJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        System.out.println("Parsing JSON: " + sb.toString()); // Debugging log
        return new JSONObject(sb.toString());
    }

    private Order getOrderByNumber(EntityManager em, String orderNumber) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber", Order.class)
                .setParameter("orderNumber", orderNumber)
                .getSingleResult();
    }

    private Message createMessage(JSONObject json, Order order) {
        Message message = new Message();
        message.setContent(json.getString("content"));
        message.setSender(validateSender(json.getString("sender")));
        message.setTimestamp(new Date());
        message.setOrder(order);
        return message;
    }

    private String validateSender(String sender) {
        if (!sender.equalsIgnoreCase("customer") && !sender.equalsIgnoreCase("delivery")) {
            throw new IllegalArgumentException("Invalid sender type");
        }
        return sender.toLowerCase();
    }

    private void persistMessage(EntityManager em, Message message) {
        em.persist(message);
    }

    private void sendSuccessResponse(HttpServletResponse response, Message message) throws IOException {
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject()
                .put("id", message.getId())
                .put("content", message.getContent())
                .put("sender", message.getSender())
                .put("timestamp", message.getTimestamp().getTime());

        System.out.println("Message sent: " + jsonResponse.toString()); // Debugging log
        response.getWriter().print(jsonResponse.toString());
    }

    private List<Message> getMessagesForOrder(EntityManager em, String orderNumber) {
        return em.createQuery(
                        "SELECT m FROM Message m WHERE m.order.orderNumber = :orderNumber ORDER BY m.timestamp",
                        Message.class)
                .setParameter("orderNumber", orderNumber)
                .getResultList();
    }

    private void sendMessagesResponse(HttpServletResponse response, List<Message> messages) throws IOException {
        JSONArray jsonArray = new JSONArray();
        for (Message message : messages) {
            jsonArray.put(new JSONObject()
                    .put("id", message.getId())
                    .put("content", message.getContent())
                    .put("sender", message.getSender())
                    .put("timestamp", message.getTimestamp().getTime())
            );
        }
        System.out.println("Messages retrieved: " + jsonArray.toString()); // Debugging log
        response.setContentType("application/json");
        response.getWriter().print(jsonArray.toString());
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    private void rollbackTransaction(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        System.err.println("Error: " + message); // Debugging log
        response.sendError(code, message);
    }
    private void closeEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    @Override
    public void destroy() {
        emf.close();
    }
}
