package controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@WebServlet("/Product/*")
public class ProductServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = emf.createEntityManager();

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is missing in the URL.");
                return;
            }

            long productId;
            try {
                productId = Long.parseLong(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
                return;
            }

            List<Object[]> results = em.createNativeQuery(
                    "SELECT p.id, p.name, p.description, p.price, p.category, p.subCategory, p.bestseller, " +
                            "pi.image, ps.size " +
                            "FROM product p " +
                            "LEFT JOIN product_images pi ON p.id = pi.product_id " +
                            "LEFT JOIN product_sizes ps ON p.id = ps.product_id " +
                            "WHERE p.id = ?"
            ).setParameter(1, productId).getResultList();

            if (results.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }

            Map<Long, Map<String, Object>> productMap = new HashMap<>();

            for (Object[] row : results) {
                Long id = ((Number) row[0]).longValue();

                if (!productMap.containsKey(id)) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("_id", id);
                    product.put("name", row[1]);
                    product.put("description", row[2]);
                    product.put("price", row[3]);
                    product.put("category", row[4]);
                    product.put("subCategory", row[5]);
                    product.put("bestseller", (Boolean) row[6]);
                    product.put("image", new HashSet<String>());
                    product.put("sizes", new HashSet<String>());

                    productMap.put(id, product);
                }

                if (row[7] != null) {
                    ((Set<String>) productMap.get(id).get("image")).add((String) row[7]);
                }

                if (row[8] != null) {
                    ((Set<String>) productMap.get(id).get("sizes")).add((String) row[8]);
                }
            }

            List<Map<String, Object>> products = new ArrayList<>(productMap.values());
            products.forEach(p -> {
                p.put("image", new ArrayList<>((Set<String>) p.get("image")));
                p.put("sizes", new ArrayList<>((Set<String>) p.get("sizes")));
            });

            Jsonb jsonb = JsonbBuilder.create();
            String json = jsonb.toJson(products);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving the product.");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}