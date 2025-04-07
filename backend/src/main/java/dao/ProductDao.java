package dao;

import jakarta.persistence.EntityManager;
import Model.Product;

import java.util.List;
public class ProductDao implements IProduct{

    private final EntityManager em;

    public ProductDao(EntityManager em) {
        this.em = em;
    }


    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    public void save(Product product) {
        em.persist(product);
    }

    public Product modify(Product updatedProduct) {
        return em.merge(updatedProduct);
    }


    public void delete(Product product) {
        if (!em.contains(product)) {
            product = em.merge(product);
        }
        em.remove(product);
    }

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
}
