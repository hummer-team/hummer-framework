package com.hummer.cache.plugin;

@FunctionalInterface
public interface SupplierEx<T> {
    T get() throws Throwable;
}
