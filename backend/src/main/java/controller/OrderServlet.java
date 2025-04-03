package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import Model.Order;
import Model.Message;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;

@WebServlet(name = "OrderServlet", urlPatterns = {"/api/orders/*"})
public class OrderServlet extends HttpServlet {
    private EntityManagerFactory emf;
    //lport dial socket
    private static final int WS_PORT = 8081;
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("ecommercePU");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        String path = req.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                sendError(resp, 400, "Invalid path");
                return;
            }

            String[] parts = path.split("/");
            if (parts.length < 3) {
                sendError(resp, 400, "Invalid path format");
                return;
            }
            String orderId = parts[1];
            String endpoint = parts[2];
            switch (endpoint) {
                case "status":
                    handleGetStatus(resp, orderId);
                    break;
                case "messages":
                    handleGetMessages(resp, orderId);
                    break;
                default:
                    sendError(resp, 400, "Unknown endpoint");
            }
        } catch (Exception e) {
            sendError(resp, 500, "Server error: " + e.getMessage());
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        String path = req.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                sendError(resp, 400, "Invalid path");
                return;
            }

            String[] parts = path.split("/");
            if (parts.length < 3 || !"messages".equals(parts[2])) {
                sendError(resp, 400, "Invalid endpoint");
                return;
            }

            String orderId = parts[1];
            JSONObject json = parseRequestBody(req);
            processNewMessage(resp, orderId, json);
            notifyWebSocket(orderId, "message", json.getString("content"));

        } catch (Exception e) {
            sendError(resp, 500, "Error processing request: " + e.getMessage());
        }
    }

    private void handleGetStatus(HttpServletResponse resp, String orderId)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        try {
            Order order = findOrder(em, orderId);
            JSONObject response = new JSONObject()
                    .put("status", order.getDelivery().getStatus().name());

            sendJsonResponse(resp, 200, response);
        } finally {
            em.close();
        }
    }

    private void handleGetMessages(HttpServletResponse resp, String orderId)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        try {
            List<Message> messages = em.createQuery(
                            "SELECT m FROM Message m WHERE m.order.orderNumber = :orderId " +
                                    "ORDER BY m.timestamp DESC", Message.class)
                    .setParameter("orderId", orderId)
                    .getResultList();

            JSONArray messagesJson = new JSONArray();
            for (Message msg : messages) {
                messagesJson.put(new JSONObject()
                        .put("id", msg.getId())
                        .put("content", msg.getContent())
                        .put("sender", msg.getSender())
                        .put("timestamp", msg.getTimestamp())
                );
            }

            sendJsonResponse(resp, 200, new JSONObject().put("messages", messagesJson));
        } finally {
            em.close();
        }
    }

    private void processNewMessage(HttpServletResponse resp, String orderId, JSONObject json)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Order order = findOrder(em, orderId);

            Message message = new Message();
            message.setContent(json.getString("content"));
            message.setSender(json.optString("sender", "customer"));
            message.setTimestamp(new Date());
            message.setOrder(order);

            em.persist(message);
            tx.commit();

            sendJsonResponse(resp, 201, new JSONObject()
                    .put("id", message.getId())
                    .put("timestamp", message.getTimestamp())
            );

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private Order findOrder(EntityManager em, String orderId) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.orderNumber = :orderId", Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    private JSONObject parseRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return new JSONObject(sb.toString());
    }

    private void notifyWebSocket(String orderId, String type, String content) {
        try (Socket socket = new Socket("localhost", WS_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            JSONObject message = new JSONObject()
                    .put("orderId", orderId)
                    .put("type", type)
                    .put("data", content)
                    .put("timestamp", new Date().getTime());

            out.println(message.toString());
        } catch (IOException e) {
            System.err.println("WebSocket notification failed: " + e.getMessage());
        }
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5174");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }

    private void sendJsonResponse(HttpServletResponse resp, int status, JSONObject data)
            throws IOException {

        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(data.toString());
    }

    private void sendError(HttpServletResponse resp, int code, String message)
            throws IOException {

        resp.setStatus(code);
        resp.setContentType("application/json");
        new JSONObject()
                .put("error", true)
                .put("message", message)
                .write(resp.getWriter());
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
