package com.social_service.model.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int page;
    int size;
    long totalElements;
    int totalPages;
    T result;
}
