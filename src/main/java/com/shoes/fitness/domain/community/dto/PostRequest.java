package com.shoes.fitness.domain.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
    private String category;
    private String title;
    private String content;
    private Long price;
    private String location;
    private String locationDetail;
    private String contact;
    private List<String> imageUrls;
}
