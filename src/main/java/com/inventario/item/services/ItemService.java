package com.inventario.item.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventario.item.models.Item;
import com.inventario.item.repositories.CustomRepository;

@Service
public class ItemService {

	@Autowired
	private CustomRepository customRepository;

	public List<Item> getAllItems(String tableName, Long parentId) {
		return customRepository.findAllItems(tableName, parentId);
	}

}
