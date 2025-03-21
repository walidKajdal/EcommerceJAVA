package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {

    private EntityManagerFactory emf;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("Initialisation du serveur...");
            emf = Persistence.createEntityManagerFactory("ecommercePU");
            EntityManager em = emf.createEntityManager();

            em.getTransaction().begin();
            DataImporter importer = new DataImporter(em);
            importer.importProducts();
            em.getTransaction().commit();

            System.out.println("Importation automatique terminée !");
            em.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'importation des produits !");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (emf != null) {
            emf.close();
        }
        System.out.println("Serveur arrêté !");
    }
}
