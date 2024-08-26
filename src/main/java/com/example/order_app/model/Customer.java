package com.example.order_app.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name="customers")
public class Customer {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name", nullable = false)
    private String first_name;

    @Column(name="last_name", nullable = false)
    private String last_name;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="phone", nullable = false)
    private String phone;

    @Column(name="city", nullable = false)
    private String city;

    @Column(name="country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "customer",
                cascade={CascadeType.PERSIST,CascadeType.MERGE,
                         CascadeType.DETACH,CascadeType.REFRESH})
    private List<Order> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Customer() {

    }

    public Customer(String first_name, String last_name, String email, String phone, String city, String country, List<Order> orders) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", orders=" + orders +
                '}';
    }
}
