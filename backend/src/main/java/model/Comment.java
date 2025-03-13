package model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlTransient;


@Entity
@Table(name="comment")
@NamedQuery(name = "findProductComments",
        query = "SELECT c from Comment c where c.product.idProduct=:productId ")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_comment")
    private int commentId ;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private  User user;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    private String comment;

    private int star;


    public Comment() {
    }

    public Comment(String comment, int star) {
        this.comment = comment;
        this.star = star;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getCommentId() {
        return commentId;
    }
    @JsonbTransient
    @XmlTransient
    public User getUser() {
        return user;
    }

    @JsonbTransient
    @XmlTransient
    public Product getProduct() {
        return product;
    }

    public String getComment() {
        return comment;
    }

    public int getStar() {
        return star;
    }

}