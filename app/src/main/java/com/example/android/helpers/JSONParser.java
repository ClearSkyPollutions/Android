package com.example.android.helpers;

@FunctionalInterface
public interface JSONParser<T> {
    void apply(T arg);
}
