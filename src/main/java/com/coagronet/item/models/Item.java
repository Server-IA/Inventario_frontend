package com.coagronet.item.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private Long id;
    private String name;

    public Item(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}