package com.store.example.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name="sales")
public class Sales {
    /*
    @Id
    @GeneratedValue(generator = "uuidSales")
    @GenericGenerator(name = "uuidSales",strategy = "uuidSales")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    */


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_product_sales")
    private Product idProductSales;

    @ManyToOne
    @JoinColumn(name = "id_user_sales")
    private User idUserSales;

    @ManyToOne
    @JoinColumn(name = "id_address_sales")
    private Address idAddressSales;

    @Column(name = "status_sale")
    private Boolean statusSale;


    @Column(name="sale_at")
    private LocalDateTime saleAt;


    public Sales(){

    }


    public Sales(Product idProductSales, User idUserSales, Address idAddressSales, Boolean statusSale, LocalDateTime saleAt) {
        this.idProductSales = idProductSales;
        this.idUserSales = idUserSales;
        this.idAddressSales = idAddressSales;
        this.statusSale = statusSale;
        this.saleAt = saleAt;
    }
}
