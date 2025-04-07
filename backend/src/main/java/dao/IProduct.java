package dao;

import Model.Product;
import java.util.List;

public interface IProduct {
    Product findById(Long id);
    void save(Product product);
    Product modify(Product updatedProduct);
    void delete(Product product);
    List<Product> findAll();
}
