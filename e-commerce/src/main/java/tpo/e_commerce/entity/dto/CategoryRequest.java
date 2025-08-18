package tpo.e_commerce.entity.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CategoryRequest {
    private String description;
}