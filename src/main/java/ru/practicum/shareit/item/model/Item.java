package ru.practicum.shareit.item.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Item {
    private Integer id;
    private int ownerId;
    private String name;
    private String description;
    private Boolean available;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
