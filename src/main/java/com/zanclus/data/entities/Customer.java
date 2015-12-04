package com.zanclus.data.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * A JPA annotated POJO representing a Customer entity
 */
@Data
@Accessors(fluent = true, chain = true)
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @Column
    @JsonProperty("name")
    private String name;

    @Column(name = "street_addr_1")
    @JsonProperty("street_addr_1")
    private String streetAddress1;

    @Column(name = "street_addr_2")
    @JsonProperty("street_addr_2")
    private String streetAddress2;

    @Column
    @JsonProperty("city")
    private String city;

    @Column
    @JsonProperty("province")
    private String province;

    @Column
    @JsonProperty("country")
    private String country;

    @Column(name = "post_code")
    @JsonProperty("post_code")
    private String postalCode;
}
