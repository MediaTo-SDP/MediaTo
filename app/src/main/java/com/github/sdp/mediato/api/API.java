package com.github.sdp.mediato.api;

import java.util.ArrayList;
import java.util.concurrent.Future;

public interface API<T> {
    Future<T> searchItem(String s);

    Future<ArrayList<T>> searchItems(String s, int count);

    Future<ArrayList<T>> trending(int count);

    void clearCache();
}
