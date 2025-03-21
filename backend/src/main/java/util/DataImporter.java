package util;

import Model.Product;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class DataImporter {

    private final EntityManager entityManager;

    public DataImporter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void importProducts() {
        System.out.println("📦 Importation des produits depuis JSON...");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("products.json")) {
            if (is == null) {
                System.out.println("❌ Fichier JSON introuvable !");
                return;
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Jsonb jsonb = JsonbBuilder.create();
            Product[] productsArray = jsonb.fromJson(jsonContent, Product[].class);

            if (productsArray == null || productsArray.length == 0) {
                System.out.println("❌ Le fichier JSON est vide ou mal formaté !");
                return;
            }

            List<Product> products = Arrays.asList(productsArray);
            for (Product product : products) {
                entityManager.persist(product);
            }

            System.out.println("✅ Produits importés avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'importation des produits !");
        }
    }
}
