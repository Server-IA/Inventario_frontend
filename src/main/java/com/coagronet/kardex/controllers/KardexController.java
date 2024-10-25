package com.coagronet.kardex.controllers;

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
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.asemblers.KardexModelAssembler;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin(origins = "*")
public class KardexController {

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private KardexMapper kardexMapper;

    @Autowired
    private KardexModelAssembler kardexModelAssembler;

    @Autowired
    private PagedResourcesAssembler<Kardex> kardexPagedResourcesAssembler;

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteKardex(@PathVariable Integer id) {
        if (kardexRepository.existsById(id)) {
            Kardex kardex = kardexRepository.findByIdAndEstadoNot(id, 2);
            kardex.setEstado(2);
            kardexRepository.save(kardex);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<EntityModel<KardexDTO>> updateKardex(
            @PathVariable Integer requestedId,
            @RequestBody KardexDTO kardexDTOUpdate) {

        // Verifica si el Kardex con el requestedId existe
        if (!kardexRepository.existsById(requestedId)) {
            return ResponseEntity.notFound().build();
        }

        // Mapea el DTO a la entidad y establece el ID
        Kardex kardex = KardexMapper.INSTANCE.toEntity(kardexDTOUpdate);
        kardex.setId(requestedId);

        // Guarda la entidad actualizada en el repositorio
        kardexRepository.save(kardex);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PagedModel<EntityModel<KardexDTO>> all(Integer estado, Pageable pageable) {
        Page<Kardex> kardexPage = kardexRepository.findByEstadoNot(2, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return kardexPagedResourcesAssembler.toModel(kardexPage, kardexModelAssembler);
    }

    @GetMapping("/{requestedId}")
    public EntityModel<KardexDTO> findKardex(@PathVariable Integer requestedId) {
        Kardex kardex = kardexRepository.findByIdAndEstadoNot(requestedId, 2);
        if (kardex != null) {
            return kardexModelAssembler.toModel(kardex);
        } else {
            throw new ResourceNotFoundException("Kardex not found");
        }
    }

    @PostMapping
    public ResponseEntity<Void> createKardex(@RequestBody KardexDTO kardexDTO, UriComponentsBuilder ucb) {
        Kardex kardex = kardexMapper.toEntity(kardexDTO);
        Kardex savedKardex = kardexRepository.save(kardex);
        URI locationOfNewKardex = ucb
                .path("/api/v1/kardex/{id}")
                .buildAndExpand(savedKardex.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewKardex).build();
    }
}
