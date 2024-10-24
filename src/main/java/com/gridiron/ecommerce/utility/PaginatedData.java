package com.gridiron.ecommerce.utility;

import lombok.Builder;

@Builder
public record PaginatedData(
        int totalPage,
        int currentSize,
        Long totalSize,
        Object data
) {
}
