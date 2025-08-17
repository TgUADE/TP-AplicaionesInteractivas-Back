package tpo.e_commerce.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
public class Category {
    private int id;
    private String description;
}
