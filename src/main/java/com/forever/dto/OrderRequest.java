package com.forever.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItemDto> items;
    private Double amount;
    private AddressDto address;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private String name;
        private Double price;
        private String size;
        private Integer quantity;
        private String image;
    }

    @Data
    public static class AddressDto {
        private String firstName;
        private String lastName;
        private String email;
        private String street;
        private String city;
        private String state;
        private String zipcode;
        private String country;
        private String phone;
    }
}
