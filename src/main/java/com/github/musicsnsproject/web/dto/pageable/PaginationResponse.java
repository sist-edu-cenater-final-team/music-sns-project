package com.github.musicsnsproject.web.dto.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class PaginationResponse<T> {
    private long page; // 현재 페이지 번호
    private long size; // 페이지당 아이템 수
    private long totalPages; // 전체 페이지 수
    private long totalItems; // 전체 아이템 수
    private List<T> items; // 페이지에 포함된 아이템 목록

}
