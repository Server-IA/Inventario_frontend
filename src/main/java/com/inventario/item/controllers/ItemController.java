package com.inventario.item.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.item.models.Item;
import com.inventario.item.services.ItemService;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@GetMapping("/{tableName}/{parentId}")
	public List<Item> getAllItems2Params(@PathVariable String tableName, @PathVariable Long parentId) {
		return itemService.getAllItems(tableName, parentId);
	}

}
