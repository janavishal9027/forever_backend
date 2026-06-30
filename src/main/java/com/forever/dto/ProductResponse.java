package com.forever.dto;

import com.forever.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private List<String> image;
    private String category;
    private String subCategory;
    private List<String> sizes;
    private Boolean bestseller;
    private Long date;

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImage(product.getImages());
        response.setCategory(product.getCategory());
        response.setSubCategory(product.getSubCategory());
        response.setSizes(product.getSizes());
        response.setBestseller(product.getBestseller());
        response.setDate(product.getDate());
        return response;
    }
}
