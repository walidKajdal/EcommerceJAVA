package Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String category;
    private String subCategory;
    private long date;
    private boolean bestseller;

    @ElementCollection(fetch = FetchType.EAGER) // Force eager fetching
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image")
    private List<String> image;

    @ElementCollection(fetch = FetchType.EAGER) // Force eager fetching
    @CollectionTable(
            name = "product_sizes", // Maps to the product_sizes table
            joinColumns = @JoinColumn(name = "product_id") // Foreign key column
    )
    @Column(name = "size") // Column in product_sizes storing size values
    private List<String> sizes;

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getSubCategory() { return subCategory; }
    public long getDate() { return date; }
    public boolean isBestseller() { return bestseller; }
    public List<String> getImage() { return image; }
    public List<String> getSizes() { return sizes; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public void setDate(long date) { this.date = date; }
    public void setBestseller(boolean bestseller) { this.bestseller = bestseller; }
    public void setImage(List<String> image) { this.image = image; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }
}
