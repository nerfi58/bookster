package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.PageDto;
import org.springframework.data.domain.Page;

public class PageMapper {

    public static <T> PageDto<T> pageToPageDto(Page<T> page) {
        return PageDto.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }
}
