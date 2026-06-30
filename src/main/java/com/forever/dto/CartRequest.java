package com.forever.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long itemId;
    private String size;
    private Integer quantity;
}
