package com.mvp.op.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class DraftItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String categoryId;
    private String condition;
    private Double price;
    private Integer availableQuantity;
    private String listingTypeId;
    private String description;
    private String originalItemId;

    @ElementCollection
    private List<String> pictures;

}