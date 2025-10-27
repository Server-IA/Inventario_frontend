package com.coagronet.productopresentacionstock.controllers;

import com.coagronet.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import com.coagronet.productopresentacionstock.services.ProductoPresentacionStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stock")
@RequiredArgsConstructor
public class ProductoPresentacionStockController {

    private final ProductoPresentacionStockService productoPresentacionStockService;

    @GetMapping
    public ResponseEntity<Page<ProductoPresentacionStockResponseDTO>>findAll(@PageableDefault Pageable pageable){
        Page<ProductoPresentacionStockResponseDTO> page = productoPresentacionStockService.findAll(pageable);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{productoPresentacionId}")
    public ResponseEntity<ProductoPresentacionStockResponseDTO>findByProductoPresentacionId(@PathVariable Long productoPresentacionId){
        return ResponseEntity.ok(productoPresentacionStockService.findByProductoPresentacionId(productoPresentacionId));
    }

}
