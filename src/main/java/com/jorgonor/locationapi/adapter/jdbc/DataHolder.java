package com.jorgonor.locationapi.adapter.jdbc;

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
