package websocket;

import Model.Delivery;
import Model.DeliveryStatus;
import Model.Message;
import Model.Order;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/order/{orderId}")
public class OrderWebSocket {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");

    // Map to hold sessions for each orderId
    private static Map<String, Set<Session>> orderSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("orderId") String orderId) throws IOException {
        System.out.println("New WebSocket connection for order: " + orderId);

        if (orderId == null || orderId.isEmpty()) {
            System.err.println("Invalid orderId: " + orderId);
            session.close();
            return;
        }

        // Check if the order exists in the database, if not create it
        EntityManager em = emf.createEntityManager();
        Order order = em.find(Order.class, orderId);

        if (order == null) {
            System.err.println("Order not found with orderId: " + orderId);
            // Optionally, create the order if it doesn't exist
            order = createOrder(orderId);  // Use the createOrder function
        }

        // Assign a role (for now, you can set it as "delivery" or "user")
        String role = "user";  // Default to 'user' role, change to 'delivery' based on the sender
        session.getUserProperties().put("role", role);

        // Store the session for the given orderId
        synchronized (orderSessions) {
            orderSessions.computeIfAbsent(orderId, k -> new HashSet<>()).add(session);
        }

        // Send the message history to the new client
        sendMessageHistory(session, orderId);
    }

    private void sendMessageHistory(Session session, String orderId) throws IOException {
        EntityManager em = emf.createEntityManager();
        try {
            List<Message> messages = em.createQuery(
                            "SELECT m FROM Message m WHERE m.order.orderNumber = :orderId ORDER BY m.timestamp DESC", Message.class)
                    .setParameter("orderId", orderId)
                    .getResultList();

            JSONArray messagesJson = new JSONArray();
            for (Message msg : messages) {
                messagesJson.put(new JSONObject()
                        .put("id", msg.getId())
                        .put("content", msg.getContent())
                        .put("sender", msg.getSender())
                        .put("timestamp", msg.getTimestamp()));
            }

            // Send history to the client
            session.getBasicRemote().sendText(messagesJson.toString());

        } catch (Exception e) {
            System.err.println("Error fetching message history: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("orderId") String orderId) throws IOException {
        if (orderId == null || orderId.isEmpty()) {
            System.err.println("Invalid orderId: " + orderId);
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) {
                System.err.println("Order not found: " + orderId);
                return;
            }

            JSONObject jsonMessage = new JSONObject(message);
            String messageType = jsonMessage.getString("type");

            if ("status_update".equals(messageType)) {
                String newStatus = jsonMessage.getString("message");

                // Update Delivery entity
                Delivery delivery = order.getDelivery();
                if (delivery == null) {
                    delivery = new Delivery();
                    delivery.setStatus(DeliveryStatus.PENDING);
                    em.persist(delivery);
                    order.setDelivery(delivery);
                }
                delivery.setStatus(DeliveryStatus.valueOf(newStatus.toUpperCase()));

                em.merge(delivery);

                System.out.println("Updated delivery status to: " + newStatus);
            } else if ("chat_message".equals(messageType)) {
                String content = jsonMessage.getString("message");
                String sender = (String) session.getUserProperties().getOrDefault("role", "user");

                Message msg = new Message();
                msg.setOrder(order);
                msg.setContent(content);
                msg.setSender(sender);
                em.persist(msg);
            }

            em.getTransaction().commit();

            // Broadcast the update
            broadcast(session, orderId, message);

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Error processing WebSocket message: " + e.getMessage());
        } finally {
            em.close();
        }
    }


    private void broadcast(Session senderSession, String orderId, String message) throws IOException {
        synchronized (orderSessions) {
            Set<Session> sessions = orderSessions.get(orderId);
            if (sessions != null) {
                for (Session client : sessions) {
                    if (client.isOpen() && client != senderSession) {
                        client.getBasicRemote().sendText(message);
                    }
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("orderId") String orderId) {
        System.out.println("WebSocket closed for order: " + orderId);

        synchronized (orderSessions) {
            Set<Session> sessions = orderSessions.get(orderId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    orderSessions.remove(orderId);
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error in WebSocket connection: " + throwable.getMessage());
    }

    public Order createOrder(String orderNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Order order = new Order();
            order.setOrderNumber(orderNumber);
            em.persist(order);
            em.getTransaction().commit();
            System.out.println("Order created with orderNumber: " + orderNumber);
            return order;
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Error creating order: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
}
