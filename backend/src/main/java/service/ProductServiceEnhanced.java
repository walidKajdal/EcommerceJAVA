package service;

import Model.Product;
import java.util.List;

public class ProductServiceEnhanced extends ProductService {

    @Override
    public List<Product> getAllProducts() {
        // Call the parent method and add additional functionality if needed
        List<Product> products = super.getAllProducts();
        // Additional processing can be done here
        return products;
    }

    public void displayProductCount() {
        List<Product> products = getAllProducts();
        System.out.println("Total number of products: " + products.size());
    }
}
