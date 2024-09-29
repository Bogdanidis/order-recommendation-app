package com.example.order_app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "id")
public class Customer extends User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "customer",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JsonManagedReference
    private List<Order> orders ;

    public Customer() {
        super();
        this.setRole("CUSTOMER");
    }

    public Customer(String username, String password, String email,
                    String lastName, String firstName, String phone, String city, String country) {
        super(username, password, email);
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.setRole("CUSTOMER");

    }


}
