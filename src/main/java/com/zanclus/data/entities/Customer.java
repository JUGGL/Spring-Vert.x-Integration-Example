package com.zanclus.data.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Created by dphillips on 11/14/15.
 */
@Data
@Accessors(fluent = true, chain = true)
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String name;

    @Column(name = "street_addr_1")
    private String streetAddress1;

    @Column(name = "street_addr_2")
    private String streetAddress2;

    @Column
    private String city;

    @Column
    private String province;

    @Column
    private String country;

    @Column(name = "post_code")
    private String postalCode;
}
