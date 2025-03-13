package model;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "adress", catalog = "")
public class Adress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdress")
    private int idAdress;

    @Basic
    @Column(name = "street")
    private String street;

    @Basic
    @Column(name = "city")
    private String city;

    @Basic
    @Column(name = "zipcode")
    private int zipcode;

    public Adress() {
    }

    public Adress(String street, String city, int zipcode) {
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
    }

    public int getIdAdress() {
        return idAdress;
    }

    public void setIdAdress(int idAdress) {
        this.idAdress = idAdress;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adress that = (Adress) o;
        return idAdress == that.idAdress && Objects.equals(street, that.street) && Objects.equals(city, that.city) && Objects.equals(zipcode, that.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAdress, street, city, zipcode);
    }
}