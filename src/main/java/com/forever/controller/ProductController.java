package com.forever.controller;

import com.forever.dto.ApiResponse;
import com.forever.dto.ProductResponse;
import com.forever.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("category") String category,
            @RequestParam("subCategory") String subCategory,
            @RequestParam("sizes") String sizesJson,
            @RequestParam(value = "bestseller", defaultValue = "false") String bestseller,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3,
            @RequestParam(value = "image4", required = false) MultipartFile image4) {

        try {
            // Parse sizes from JSON string
            String cleaned = sizesJson.replaceAll("[\\[\\]\"]", "");
            List<String> sizes = Arrays.asList(cleaned.split(","));

            // Collect non-null images
            List<MultipartFile> images = new ArrayList<>();
            if (image1 != null && !image1.isEmpty()) images.add(image1);
            if (image2 != null && !image2.isEmpty()) images.add(image2);
            if (image3 != null && !image3.isEmpty()) images.add(image3);
            if (image4 != null && !image4.isEmpty()) images.add(image4);

            productService.addProduct(name, description, price, category, subCategory,
                    sizes, "true".equals(bestseller), images.toArray(new MultipartFile[0]));

            return ResponseEntity.ok(ApiResponse.success("Product Added"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listProducts() {
        try {
            List<ProductResponse> products = productService.listProducts();
            return ResponseEntity.ok(Map.of("success", true, "products", products));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeProduct(@RequestBody Map<String, Long> body) {
        try {
            productService.removeProduct(body.get("id"));
            return ResponseEntity.ok(ApiResponse.success("Product Removed"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<?> getProduct(@RequestBody Map<String, Long> body) {
        try {
            ProductResponse product = productService.getProduct(body.get("productId"));
            return ResponseEntity.ok(Map.of("success", true, "product", product));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
