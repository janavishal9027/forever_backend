package com.forever.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @Column(nullable = false)
    private Double amount;

    // Address fields
    @Column(name = "address_first_name")
    private String firstName;

    @Column(name = "address_last_name")
    private String lastName;

    @Column(name = "address_email")
    private String email;

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_state")
    private String state;

    @Column(name = "address_zipcode")
    private String zipcode;

    @Column(name = "address_country")
    private String country;

    @Column(name = "address_phone")
    private String phone;

    @Column(nullable = false)
    private String status = "Order Placed";

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private Boolean payment = false;

    @Column(name = "order_date", nullable = false)
    private Long date;
}
