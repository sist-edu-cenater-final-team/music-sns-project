package com.github.musicsnsproject.web.dto.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ScrollResponse<T> {
    private int page; // 현재 페이지 번호
    private int size; // 페이지당 아이템 수
    private List<T> items; // 페이지에 포함된 아이템 목록

    public boolean isHasNext() {
        return items != null && items.size() > size;
    }
    public List<T> getItems() {
        if (items != null && items.size() > size) {
            return items.subList(0, size);
        }
        return items;
    }

}
