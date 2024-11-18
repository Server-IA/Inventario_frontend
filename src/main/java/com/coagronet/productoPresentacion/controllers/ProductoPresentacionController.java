package com.coagronet.productoPresentacion.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.productoPresentacion.ProductoPresentacion;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
import com.coagronet.productoPresentacion.mappers.ProductoPresentacionMapper;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto-presentacion")
public class ProductoPresentacionController {

    @Autowired
    private ProductoPresentacionMapper productoPresentacionMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private ProductoPresentacionRepository productoPresentacionRepository;

    @GetMapping
    private ResponseEntity<Page<ProductoPresentacionDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(productoPresentacionRepository.findByEstadoNot(2, pageable)
                .map(ProductoPresentacionMapper.INSTANCE::toDto));
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<ProductoPresentacionDTO> findById(@PathVariable Integer requestedId) {
        ProductoPresentacion productoPresentacion = productoPresentacionRepository.findByIdAndEstadoNot(requestedId, 2);
        ProductoPresentacionDTO productoPresentacionDTO = productoPresentacionMapper.toDto(productoPresentacion);
        if (productoPresentacion != null) {
            return ResponseEntity.ok(productoPresentacionDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createProductoPresentacion(
            @RequestBody ProductoPresentacionDTO productoPresentacionDTO,
            UriComponentsBuilder ucb) {
        ProductoPresentacion productoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionDTO);
        Estado nuevoEstado = estadoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
        productoPresentacion.setEstado(nuevoEstado);
        productoPresentacionRepository.save(productoPresentacion);
        URI locationOfNewProductoPresentacion = ucb
                .path("/api/v1/producto-presentacion/{id}")
                .buildAndExpand(productoPresentacion.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProductoPresentacion).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putProductoPresentacion(@PathVariable Integer requestedId,
            @RequestBody ProductoPresentacionDTO productoPresentacionUpdate) {
        ProductoPresentacion productoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionUpdate);
        productoPresentacionRepository.findByIdAndEstadoNot(requestedId, 2);
        if (null != productoPresentacion) {
            productoPresentacion.setId(requestedId);
            productoPresentacionRepository.save(productoPresentacion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteProductoPresentacion(@PathVariable Integer id) {
        if (productoPresentacionRepository.existsByIdAndEstadoNot(id, 2)) {
            ProductoPresentacion productoPresentacion = productoPresentacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("ProductoPresentacion not found with id: " + id));
            Estado nuevoEstado = estadoRepository.findById(2)
                    .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));
            productoPresentacion.setEstado(nuevoEstado);
            productoPresentacionRepository.save(productoPresentacion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
