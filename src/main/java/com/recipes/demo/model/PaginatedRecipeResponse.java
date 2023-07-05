package com.recipes.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedRecipeResponse {
    private List<RecipeDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PaginatedRecipeResponse(Page<RecipeDTO> page) {
        this.content = page.getContent();
        this.pageNo = page.getPageable().getPageNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    public PaginatedRecipeResponse(List<RecipeDTO> content) {
        this.content = content;
    }
}


