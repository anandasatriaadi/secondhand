package com.binaracademy.secondhand.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_offer")
public class ProductOffer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long productId;
    private Double offerPrice;

    @ManyToOne()
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;
}
