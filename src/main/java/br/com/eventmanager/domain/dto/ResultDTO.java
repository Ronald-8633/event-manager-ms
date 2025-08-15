package br.com.eventmanager.domain.dto;

import lombok.Getter;

@Getter
public class ResultDTO<T> {

    private final boolean success;
    private final T data;
    private final String error;

    private ResultDTO(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ResultDTO<T> success(T data) {
        return new ResultDTO<>(true, data, null);
    }

    public static <T> ResultDTO<T> failure(String error) {
        return new ResultDTO<>(false, null, error);
    }

    public boolean isFailure() {
        return !success;
    }

    public T getOrThrow() {
        if (isFailure()) {
            throw new RuntimeException(error);
        }
        return data;
    }
}
