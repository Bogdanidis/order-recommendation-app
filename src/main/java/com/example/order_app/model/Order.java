package com.example.order_app.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Add cascading info
    //Maybe @Column(name="customer_id")
    @JoinColumn(name="customer_id")
    @ManyToOne(fetch = FetchType.EAGER,
            cascade={CascadeType.PERSIST,CascadeType.MERGE,
                     CascadeType.DETACH,CascadeType.REFRESH})
    private Customer customer;


    @OneToMany(mappedBy = "order",
            cascade={CascadeType.PERSIST,CascadeType.MERGE,
                    CascadeType.DETACH,CascadeType.REFRESH})
    private List<OrderProduct> order_products;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderProduct> getOrder_products() {
        return order_products;
    }

    public void setOrder_products(List<OrderProduct> order_products) {
        this.order_products = order_products;
    }

    public Order() {

    }

    public Order(Customer customer, List<OrderProduct> order_products) {
        this.customer = customer;
        this.order_products = order_products;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", order_products=" + order_products +
                '}';
    }
}
