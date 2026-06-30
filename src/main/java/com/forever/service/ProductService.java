package com.forever.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.forever.dto.ProductResponse;
import com.forever.entity.Product;
import com.forever.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    public void addProduct(String name, String description, Double price, String category,
                           String subCategory, List<String> sizes, Boolean bestseller,
                           MultipartFile[] images) {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                try {
                    Map<?, ?> uploadResult = cloudinary.uploader().upload(image.getBytes(),
                            ObjectUtils.asMap("resource_type", "image"));
                    imageUrls.add((String) uploadResult.get("secure_url"));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image: " + e.getMessage());
                }
            }
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setSizes(sizes);
        product.setBestseller(bestseller != null ? bestseller : false);
        product.setImages(imageUrls);
        product.setDate(System.currentTimeMillis());

        productRepository.save(product);
    }

    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public void removeProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductResponse.from(product);
    }
}
