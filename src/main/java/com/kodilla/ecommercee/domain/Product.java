package com.kodilla.ecommercee.domain;

import jdk.jfr.Unsigned;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity(name = "PRODUCTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Product {

    @Id
    @GeneratedValue
    @NotNull
    @Unsigned
    @Column(name = "ID_PRODUCT", unique = true)
    private Long id;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "QUANTITY")
    private int quantity;

    @NotNull
    @Column(name = "PRICE")
    private double price;

    @Column(name = "product_description")
    private String productDescription;



    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GROUP")
    private Group group;

}
