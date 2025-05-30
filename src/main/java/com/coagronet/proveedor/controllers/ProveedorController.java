package com.coagronet.proveedor.controllers;

import com.coagronet.proveedor.dtos.ProveedorDTO;
import com.coagronet.proveedor.services.ProveedorService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final UriBuilderUtil uriBuilderUtil;


    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> findAll () {
        List<ProveedorDTO> produccionDTOList = proveedorService.findAll();

        return produccionDTOList.isEmpty()?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(produccionDTOList);

    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ProveedorDTO> findById (@PathVariable Long requestedId) {
        return proveedorService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<Void> crearProduccion(@RequestBody @Valid ProveedorDTO produccionDTO, UriComponentsBuilder ucb) {
        ProveedorDTO savedProveedorDTO = proveedorService.create(produccionDTO);

        URI locationOfNewProduccion = uriBuilderUtil.buildProveedorUri(savedProveedorDTO.getId(), ucb);
        return ResponseEntity.created(locationOfNewProduccion).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> actualizarProduccion(@PathVariable Long requestedId,
                                                     @RequestBody ProveedorDTO produccionDTO) {

        proveedorService.update(requestedId, produccionDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> eliminarProduccion(@PathVariable Long requestedId) {
        proveedorService.delete(requestedId);
        return ResponseEntity.noContent().build();
    }



}
