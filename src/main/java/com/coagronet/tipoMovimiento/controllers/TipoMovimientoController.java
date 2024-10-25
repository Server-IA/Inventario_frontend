package com.coagronet.tipoMovimiento.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.dtos.DatosListadoTipoMovimiento;
import com.coagronet.tipoMovimiento.reposritories.TipoMovimientoRepository;

@RestController
@RequestMapping("/api/v1/tipoMovimiento")
@CrossOrigin(origins = "*")
public class TipoMovimientoController {

    @Autowired
    private TipoMovimientoRepository tipoMovimientoRepository;

    @GetMapping("/short")
    public ResponseEntity<List<DatosListadoTipoMovimiento>> listadoTipoMovimiento() {
        Sort sort = Sort.by(Sort.Direction.ASC, "nombre");
        List<TipoMovimiento> tipoMovimientos = tipoMovimientoRepository.findByEstadoNot(2, sort);
        List<DatosListadoTipoMovimiento> datosListadoTipoMovimiento = tipoMovimientos.stream().map(DatosListadoTipoMovimiento::new).collect(Collectors.toList());
        return ResponseEntity.ok(datosListadoTipoMovimiento);
    }

}
