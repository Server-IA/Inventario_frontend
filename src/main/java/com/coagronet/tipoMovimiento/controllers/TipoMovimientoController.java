package com.coagronet.tipoMovimiento.controllers;

import java.net.URI;
import java.util.List;
import com.coagronet.tipoMovimiento.dtos.TipoMovimientoDTO;
import com.coagronet.tipoMovimiento.services.TipoMovimientoService;
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
@RequestMapping("/api/v1/tipo_movimiento")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoMovimientoController {

        private final TipoMovimientoService tipoMovimientoService;
        private final UriBuilderUtil uriBuilderUtil;


        @GetMapping
        public ResponseEntity<List<TipoMovimientoDTO>> findAll () {
                List<TipoMovimientoDTO> tipoMovimientoDTOList = tipoMovimientoService.findAll();

                return tipoMovimientoDTOList.isEmpty()?
                        ResponseEntity.noContent().build()
                        : ResponseEntity.ok(tipoMovimientoDTOList);

        }

        @GetMapping("/{requestedId}")
        public ResponseEntity<TipoMovimientoDTO> findById (@PathVariable Long requestedId) {
                return tipoMovimientoService.findById(requestedId).map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());

        }


        @PostMapping
        public ResponseEntity<Void> crearTipoMovimiento(@RequestBody @Valid TipoMovimientoDTO tipoMovimientoDTO, UriComponentsBuilder ucb) {
                TipoMovimientoDTO savedTipoMovimientoDTO = tipoMovimientoService.create(tipoMovimientoDTO);

                URI locationOfNewTipoMovimiento = uriBuilderUtil.buildTipoMovimientoUri(savedTipoMovimientoDTO.getId(), ucb);
                return ResponseEntity.created(locationOfNewTipoMovimiento).build();
        }

        @PutMapping("/{requestedId}")
        public ResponseEntity<Void> actualizarTipoMovimiento(@PathVariable Long requestedId,
                                                     @RequestBody TipoMovimientoDTO tipoMovimientoDTO) {

                tipoMovimientoService.update(requestedId, tipoMovimientoDTO);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{requestedId}")
        public ResponseEntity<Void> eliminarTipoMovimiento(@PathVariable Long requestedId) {
                tipoMovimientoService.delete(requestedId);
                return ResponseEntity.noContent().build();
        }

}