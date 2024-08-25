package com.example.order_app.model;

import jakarta.persistence.*;

@Entity
@Table(name="order_products")
public class OrderProduct {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id")
    private Long order_id;

    @Column(name="product_id")
    private Long product_id;

    //Maybe @Column(name="customer_id")
    @JoinColumn(name="product_id",insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.EAGER,
            cascade={CascadeType.PERSIST,CascadeType.MERGE,
                    CascadeType.DETACH,CascadeType.REFRESH})
    private Product product;

    @JoinColumn(name="order_id",insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.EAGER,
            cascade={CascadeType.PERSIST,CascadeType.MERGE,
                    CascadeType.DETACH,CascadeType.REFRESH})
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderProduct() {

    }

    public OrderProduct(Order order, Product product, Long product_id, Long order_id) {
        this.order = order;
        this.product = product;
        this.product_id = product_id;
        this.order_id = order_id;
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", order_id=" + order_id +
                ", product_id=" + product_id +
                ", product=" + product +
                ", order=" + order +
                '}';
    }
}
