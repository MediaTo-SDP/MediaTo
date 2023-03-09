package com.github.sdp.mediato.api;

import java.util.ArrayList;

public interface API<T> {
    public T searchItem(String s);

    public ArrayList<T> searchItems(String s, int count);

    public T randomItem();

    ArrayList<T> randomItems(int count);
}
