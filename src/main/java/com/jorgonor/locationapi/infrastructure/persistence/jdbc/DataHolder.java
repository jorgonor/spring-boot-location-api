package com.jorgonor.locationapi.infrastructure.persistence.jdbc;

import lombok.Data;

@Data
public final class DataHolder<T>  {
    T value;

    public DataHolder() {
        this.value = null;
    }

    public DataHolder(T value) {
        this.value = value;
    }
}
