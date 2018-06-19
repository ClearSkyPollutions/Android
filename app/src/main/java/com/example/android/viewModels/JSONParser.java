package com.example.android.viewModels;

@FunctionalInterface
public interface JSONParser<T> {
    void apply(T arg);
}
