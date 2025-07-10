package com.coagronet.productoLocalizacion.controllers;

import com.coagronet.productoLocalizacion.services.ProductoLocalizacionService;
import com.coagronet.productoLocalizacion.dtos.ProductoLocalizacionDTO;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/producto_localizacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoLocalizacionController {

    private final ProductoLocalizacionService productoLocalizacionService;
    private final UriBuilderUtil uriBuilderUtil;



    @GetMapping
    public ResponseEntity<List<ProductoLocalizacionDTO>> findAll () {
        List<ProductoLocalizacionDTO> productoLocalizacionDTOList = productoLocalizacionService.findAll();

        return productoLocalizacionDTOList.isEmpty()?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(productoLocalizacionDTOList);

    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ProductoLocalizacionDTO> findById (@PathVariable Long requestedId) {
        return productoLocalizacionService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<Void> crearProductoLocalizacion(@RequestBody @Valid ProductoLocalizacionDTO productoLocalizacionDTO, UriComponentsBuilder ucb) {
        ProductoLocalizacionDTO savedProductoLocalizacionDTO = productoLocalizacionService.create(productoLocalizacionDTO);

        URI locationOfNewProductoLocalizacion = uriBuilderUtil.buildProductoLocalizacionUri(savedProductoLocalizacionDTO.getId(), ucb);
        return ResponseEntity.created(locationOfNewProductoLocalizacion).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> actualizarProductoLocalizacion(@PathVariable Long requestedId,
                                                    @RequestBody ProductoLocalizacionDTO productoLocalizacionDTO) {

        productoLocalizacionService.update(requestedId, productoLocalizacionDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> eliminarProductoLocalizacion(@PathVariable Long requestedId) {
        productoLocalizacionService.delete(requestedId);
        return ResponseEntity.noContent().build();
    }
}
