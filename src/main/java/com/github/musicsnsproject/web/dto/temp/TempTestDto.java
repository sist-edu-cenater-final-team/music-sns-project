package com.github.musicsnsproject.web.dto.temp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TempTestDto {
    private String name;
    private String contents;
    private List<String> imageUrls;
}
