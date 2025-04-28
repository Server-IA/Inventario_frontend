package com.coagronet.item;

import com.coagronet.item.models.Item;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;

@Entity
@SqlResultSetMapping(name = "ItemMapping", classes = @ConstructorResult(targetClass = Item.class, columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "name", type = String.class)
}))
public class SqlResultMapping {
    @Id
    private Long id; // This is just a dummy entity to hold the result set mapping
}
