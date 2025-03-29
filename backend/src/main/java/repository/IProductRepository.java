package repository;

import Model.Product;
import java.util.List;

public interface IProductRepository {
    List<Product> findAll();
    Product findById(Long id);
    void save(Product product);
    void update(Product product);
    void delete(Long id);
}
