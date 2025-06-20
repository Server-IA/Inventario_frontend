package com.coagronet.inventario.controllers;

import com.coagronet.inventario.VistaEmpresaInventario;
import com.coagronet.inventario.services.VistaEmpresaInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/control_inventario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EmpresaInventarioController {

    private final VistaEmpresaInventarioService vistaEmpresaInventarioService;

    @GetMapping
    public ResponseEntity<List<VistaEmpresaInventario>> findAllInventariosDeEmpresa() {

        List<VistaEmpresaInventario> vistaEmpresaInventarioList =
                vistaEmpresaInventarioService.findByInvEmpresaId();

        return vistaEmpresaInventarioList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(vistaEmpresaInventarioList);
    }
}
