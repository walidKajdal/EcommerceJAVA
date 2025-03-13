package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name="user")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")
    private int idUser;

    @Column(name="name")
    private String name;

    public String getName() {
        return name;
    }
    @Column(name="surname")
    private String surname;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @OneToMany (mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private ArrayList<Comment> comments;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="id_adress")
    private Adress address;

    public User() {
    }

    public User(String name, String surname, String email, String password) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.comments = new ArrayList<>();

    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    public void setAddress(Adress address) {
        this.address = address;
    }

    public int getIdUser() {
        return idUser;
    }
    public String getSurname() {
        return surname;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public ArrayList<Comment> getComments() {
        return comments;
    }
    public Adress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return idUser == that.idUser && name.equals(that.name) && surname.equals(that.surname) && email.equals(that.email) &&
                password.equals(that.password) && address.equals(that.address) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, name, surname, email, password, address, comments);
    }
}