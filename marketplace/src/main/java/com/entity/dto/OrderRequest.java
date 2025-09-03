package com.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID id;
    private UUID userId;
    private List<UUID> productIds;
}
