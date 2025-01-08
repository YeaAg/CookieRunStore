package com.koreait.store.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductDTO implements Cloneable {
    @EqualsAndHashCode.Include
    private Integer no;
    private String name;
    private Integer price;
    @ToString.Exclude
    private String detail;
    private LocalDateTime uploadedAt;
    private CategoryDTO category;
    @EqualsAndHashCode.Include
    private TreeSet<ProductOptionDTO> options;
    private List<ProductImageDTO> images;

    @Override
    public ProductDTO clone() {
        try {
            ProductDTO clone = (ProductDTO) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}