package com.coagronet.item.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coagronet.item.models.Item;
import com.coagronet.item.repositories.CustomRepository;

@Service
public class ItemService {

    @Autowired
    private CustomRepository customRepository;

    public List<Item> getAllItems(String tableName) {
        return customRepository.findAllItems(tableName);
    }
}
