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
@Table(name = "product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
    private String name;
    private String description;
    private Double price;
    private String address;
	private Long userId;
	private Long categoryId;

    @ManyToOne()
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;
}
