package dev.gtech.bifrost.bifrostapi.models.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericResult<T> {
    private final T data;
}
