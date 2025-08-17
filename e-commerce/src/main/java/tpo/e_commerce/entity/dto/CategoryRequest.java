package tpo.e_commerce.entity.dto;

import lombok.Data;
import lombok.Builder;

@Data
public class CategoryRequest {
    private int id;
    private String description;
}
