package com.store.example.model;


import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_category_product")
    private Category idCategoryProduct;

    @OneToOne
    @JoinColumn(name = "id_provider_product")
    private Provider idProviderProduct;

    private String name;

    private Double price;

    private String description;

    @Column(name = "path_img")
    private String PathImg;

    private Integer discount;

    @Column(name="sold_out")
    private Boolean soldOut;







}
