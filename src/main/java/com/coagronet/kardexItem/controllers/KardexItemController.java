package com.coagronet.kardexItem.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.coagronet.exceptionHandler.ResourceNotFoundException;
import com.coagronet.kardexItem.KardexItem;
import com.coagronet.kardexItem.asemblers.KardexItemModelAssembler;
import com.coagronet.kardexItem.dtos.KardexItemDTO;
import com.coagronet.kardexItem.mappers.KardexItemMapper;
import com.coagronet.kardexItem.repositories.KardexItemRepository;

@RestController
@RequestMapping("/api/v1/kardexItem")
@CrossOrigin(origins = "*")
public class KardexItemController {

    @Autowired
    private KardexItemRepository kardexItemRepository;

    @Autowired
    private KardexItemMapper kardexItemMapper;

    @Autowired
    KardexItemModelAssembler kardexItemModelAssembler;

    @Autowired
    private PagedResourcesAssembler<KardexItem> kardexItemPagedResourcesAssembler;

    @GetMapping
    public PagedModel<EntityModel<KardexItemDTO>> all(Integer estado, Pageable pageable) {
        Page<KardexItem> kardexItemPage = kardexItemRepository.findByEstadoNot(2, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return kardexItemPagedResourcesAssembler.toModel(kardexItemPage, kardexItemModelAssembler);
    }

    @GetMapping("/{requestedId}")
    public EntityModel<KardexItemDTO> findKardexItem(@PathVariable Integer requestedId) {
        KardexItem kardexItem = kardexItemRepository.findByIdAndEstadoNot(requestedId, 2);
        if (kardexItem != null) {
            return kardexItemModelAssembler.toModel(kardexItem);
        } else {
            throw new ResourceNotFoundException("KardexItem not found");
        }
    }

    @PostMapping
    public ResponseEntity<Void> createKardexItem(@RequestBody KardexItemDTO kardexItemDTO, UriComponentsBuilder ucb) {
        KardexItem kardexItem = kardexItemMapper.toEntity(kardexItemDTO);
        KardexItem savedKardexItem = kardexItemRepository.save(kardexItem);
        URI locationOfNewKardexItem = ucb
                .path("/api/v1/kardexItem/{id}")
                .buildAndExpand(savedKardexItem.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewKardexItem).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<KardexItemDTO>> updateKardexItem(
            @PathVariable Integer requestedId,
            @RequestBody KardexItemDTO kardexItemDTOUpdate) {

        // Verifica si el KardexItem existe
        if (!kardexItemRepository.existsById(requestedId)) {
            return ResponseEntity.notFound().build();
        }

        // Mapea y actualiza
        KardexItem kardexItem = KardexItemMapper.INSTANCE.toEntity(kardexItemDTOUpdate);
        kardexItem.setId(requestedId);
        kardexItemRepository.save(kardexItem);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteKardexItem(@PathVariable Integer id) {
        if (kardexItemRepository.existsById(id)) {
            KardexItem kardexItem = kardexItemRepository.findByIdAndEstadoNot(id, 2);
            kardexItem.setEstado(2);
            kardexItemRepository.save(kardexItem);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
