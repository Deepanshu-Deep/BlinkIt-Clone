package com.grocery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double price;
    private String description;
    private Integer stock;
    private String categoryName;
    private Long categoryId;
    private String imageUrl;


}

