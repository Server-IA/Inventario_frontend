package com.coagronet.productoPresentacion.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
import com.coagronet.exceptionHandler.ResourceNotFoundException;
import com.coagronet.productoPresentacion.ProductoPresentacion;
import com.coagronet.productoPresentacion.dtos.ProductoPresentacionDTO;
import com.coagronet.productoPresentacion.mappers.ProductoPresentacionMapper;
import com.coagronet.productoPresentacion.repositories.ProductoPresentacionRepository;
//import com.coagronet.productoPresentacion.services.ProductoPresentacionService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/producto-presentaciones")
public class ProductoPresentacionController {

    // @Autowired
    // private ProductoPresentacionService productoPresentacionService;

    @Autowired
    private ProductoPresentacionMapper productoPresentacionMapper;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private ProductoPresentacionRepository productoPresentacionRepository;

    @Autowired
    private PagedResourcesAssembler<ProductoPresentacionDTO> pagedResourcesAssembler;

    @GetMapping("/{requestedId}")
    private ResponseEntity<ProductoPresentacionDTO> findById(@PathVariable Integer requestedId) {
        ProductoPresentacion productoPresentacion = productoPresentacionRepository.findByIdAndEstadoNot(requestedId, 2);
        ProductoPresentacionDTO reqProductoPresentacionDTO = productoPresentacionMapper.toDto(productoPresentacion);
        if (productoPresentacion != null) {
            return ResponseEntity.ok(reqProductoPresentacionDTO);
        } else {
            throw new ResourceNotFoundException("ProductoPresentacion not found");
        }
    }

    @GetMapping
    private ResponseEntity<PagedModel<EntityModel<ProductoPresentacionDTO>>> findAll(
            @PageableDefault Pageable paginacion) {
        Page<ProductoPresentacion> productoPresentacionPage = productoPresentacionRepository.findByEstadoNot(2,
                paginacion);
        Page<ProductoPresentacionDTO> productoPresentacionDTOPage = productoPresentacionPage
                .map(productoPresentacionMapper::toDto);

        PagedModel<EntityModel<ProductoPresentacionDTO>> pagedModel = pagedResourcesAssembler
                .toModel(productoPresentacionDTOPage);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductoPresentacionDTO productoPresentacionDTO,
            UriComponentsBuilder ucb) {
        ProductoPresentacion productoPresentacion = productoPresentacionMapper.toEntity(productoPresentacionDTO);
        ProductoPresentacion savedProductoPresentacion = productoPresentacionRepository.save(productoPresentacion);
        URI locationOfNewProductoPresentacion = ucb
                .path("/api/v1/producto-presentaciones/{id}")
                .buildAndExpand(savedProductoPresentacion.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewProductoPresentacion).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<ProductoPresentacionDTO>> update(
            @PathVariable Integer requestedId,
            @RequestBody ProductoPresentacionDTO productoPresentacionDTO) {

        // Verifica si el Kardex con el requestedId existe
        if (!productoPresentacionRepository.existsById(requestedId)) {
            return ResponseEntity.notFound().build();
        }

        // Mapea el DTO a la entidad y establece el ID
        ProductoPresentacion productoPresentacion = ProductoPresentacionMapper.INSTANCE
                .toEntity(productoPresentacionDTO);
        productoPresentacion.setId(requestedId);

        // Guarda la entidad actualizada en el repositorio
        productoPresentacionRepository.save(productoPresentacion);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable Integer id) {
        ProductoPresentacion productoPresentacion = productoPresentacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Almacen not found with id: " + id));

        Estado nuevoEstado = estadoRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));

        productoPresentacion.setEstado(nuevoEstado);
        productoPresentacionRepository.save(productoPresentacion);
        return ResponseEntity.noContent().build();
    }
}
