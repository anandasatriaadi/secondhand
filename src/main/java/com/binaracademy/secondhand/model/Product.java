package com.binaracademy.secondhand.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String name;
    private String description;

    @ManyToOne()
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;

	@OneToMany(mappedBy = "product")
    private Set<ProductImage> productImages;
    
	@OneToMany(mappedBy = "product")
    private Set<ProductOffer> productOffers;
}
