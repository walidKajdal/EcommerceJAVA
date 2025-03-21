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

import Model.Product;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@WebServlet("/import")
public class DataImportServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("⚡ Avant la requête SQL");

            List<Object[]> results = em.createNativeQuery(
                    "SELECT p.id, p.name, p.description, p.price, p.category, p.subCategory, p.bestseller, " +
                            "pi.image, ps.size " +
                            "FROM product p " +
                            "LEFT JOIN product_images pi ON p.id = pi.product_id " +
                            "LEFT JOIN product_sizes ps ON p.id = ps.product_id"
            ).getResultList();

            for (Object[] row : results) {
                System.out.println("Produit: " + Arrays.toString(row));
            }

            Map<Long, Map<String, Object>> productMap = new HashMap<>();

            for (Object[] row : results) {
                Long productId = ((Number) row[0]).longValue();

                if (!productMap.containsKey(productId)) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("_id", productId);
                    product.put("name", row[1]);
                    product.put("description", row[2]);
                    product.put("price", row[3]);
                    product.put("category", row[4]);
                    product.put("subCategory", row[5]);
                    product.put("bestseller", (Boolean) row[6]);
                    product.put("image", new HashSet<String>());
                    product.put("sizes", new HashSet<String>());

                    productMap.put(productId, product);
                }

                if (row[7] != null) {
                    ((Set<String>) productMap.get(productId).get("image")).add((String) row[7]);
                }

                if (row[8] != null) {
                    ((Set<String>) productMap.get(productId).get("sizes")).add((String) row[8]);
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
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, " Erreur lors de la récupération des produits !");
        } finally {
            em.close();
        }
    }
}
