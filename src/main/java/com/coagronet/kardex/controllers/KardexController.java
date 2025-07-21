package com.coagronet.kardex.controllers;

import java.net.URI;
import java.util.List;

import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.services.KardexService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/kardex")
@RequiredArgsConstructor
public class KardexController {

    private final KardexService kardexService;
    private final UriBuilderUtil uriBuilderUtil;



    @GetMapping
    public ResponseEntity<List<KardexDTO>> findAll () {
        List<KardexDTO> kardexDTOList = kardexService.findAll();

        return kardexDTOList.isEmpty()?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(kardexDTOList);

    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<KardexDTO> findById (@PathVariable Long requestedId) {
        return kardexService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<Void> crearKardex(@RequestBody @Valid KardexDTO kardexDTO, UriComponentsBuilder ucb) {
        KardexDTO savedKardexDTO = kardexService.create(kardexDTO);

        URI locationOfNewKardex = uriBuilderUtil.buildKardexUri(savedKardexDTO.getId(), ucb);
        return ResponseEntity.created(locationOfNewKardex).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> actualizarKardex(@PathVariable Long requestedId,
                                                     @RequestBody KardexDTO kardexDTO) {

        kardexService.update(requestedId, kardexDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> eliminarKardex(@PathVariable Long requestedId) {
        kardexService.delete(requestedId);
        return ResponseEntity.noContent().build();
    }

}
