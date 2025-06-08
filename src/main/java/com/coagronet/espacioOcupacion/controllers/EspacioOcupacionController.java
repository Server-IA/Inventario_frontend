package com.coagronet.espacioOcupacion.controllers;

import java.net.URI;
import java.util.List;
import com.coagronet.espacioOcupacion.services.EspacioOcupacionService;
import com.coagronet.espacioOcupacion.dtos.EspacioOcupacionDTO;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/espacio_ocupacion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EspacioOcupacionController {

    private final EspacioOcupacionService espacioOcupacionService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<List<EspacioOcupacionDTO>> findAll () {
        List<EspacioOcupacionDTO> espacioOcupacionDTOList = espacioOcupacionService.findAll();

        return espacioOcupacionDTOList.isEmpty()?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(espacioOcupacionDTOList);

    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<EspacioOcupacionDTO> findById (@PathVariable Long requestedId) {
        return espacioOcupacionService.findById(requestedId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<Void> crearEspacioOcupacion(@RequestBody @Valid EspacioOcupacionDTO espacioOcupacionDTO, UriComponentsBuilder ucb) {
        EspacioOcupacionDTO savedEspacioOcupacionDTO = espacioOcupacionService.create(espacioOcupacionDTO);

        URI locationOfNewEspacioOcupacion = uriBuilderUtil.buildEspacioOcupacionUri(savedEspacioOcupacionDTO.getId(), ucb);
        return ResponseEntity.created(locationOfNewEspacioOcupacion).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> actualizarEspacioOcupacion(@PathVariable Long requestedId,
                                                    @RequestBody EspacioOcupacionDTO espacioOcupacionDTO) {

        espacioOcupacionService.update(requestedId, espacioOcupacionDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> eliminarEspacioOcupacion(@PathVariable Long requestedId) {
        espacioOcupacionService.delete(requestedId);
        return ResponseEntity.noContent().build();
    }

}
