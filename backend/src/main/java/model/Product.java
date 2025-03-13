package model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name = "findAllProduct",
        query = "SELECT p from Product p ")
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_product")
    private int idProduct;

    private String name;

    private double price;

    private String description;

    private int stock;

    private int star;


    @OneToMany (mappedBy = "product",cascade = CascadeType.ALL)
    private ArrayList<Comment> comments;
    @Transient
    private int orderQuantity;

    public Product() {
    }

    public Product(String name, double price, String description, int stock, int star) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.star = star;
        this.comments = new ArrayList<>();
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setStar(int star) {
        this.star = star;
    }
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public int getIdProduct() {
        return idProduct;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public String getDescription() {
        return description;
    }
    public int getStock() {
        return stock;
    }
    public int getStar() {
        return star;
    }
    public ArrayList<Comment> getComments() {
        return comments;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
    public int getCommentSize(){
        return  comments.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return idProduct == that.idProduct && stock == that.stock && name.equals(that.name)  && description.equals(that.description) && star == that.star && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, name, price, description, stock, star, comments);
    }
}