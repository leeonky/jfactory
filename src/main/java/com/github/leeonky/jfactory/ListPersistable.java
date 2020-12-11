package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.List;

class ListPersistable implements Persistable {
    private List<Object> data = new ArrayList<>();

    @Override
    public void save(Object object) {
        data.add(object);
    }

    public List<Object> getAll() {
        return data;
    }
}
