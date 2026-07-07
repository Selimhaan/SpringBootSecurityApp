package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentResponseDto {
    private Long id;
    private String name;
    private String contentType;
    private Long size;
    private String ownerName;
}
