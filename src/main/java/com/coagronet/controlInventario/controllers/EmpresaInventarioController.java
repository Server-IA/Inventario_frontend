package com.coagronet.controlInventario.controllers;

import com.coagronet.controlInventario.VistaEmpresaInventario;
import com.coagronet.controlInventario.VistaInventarioProducto;
import com.coagronet.controlInventario.services.VistaEmpresaInventarioService;
import com.coagronet.controlInventario.services.VistaInventarioProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/control_inventario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EmpresaInventarioController {

    private final VistaEmpresaInventarioService vistaEmpresaInventarioService;
    private final VistaInventarioProductoService vistaInventarioProductoService;

    @GetMapping("/empresa_inventario")
    public ResponseEntity<List<VistaEmpresaInventario>> findAllInventariosDeEmpresa() {

        List<VistaEmpresaInventario> vistaEmpresaInventarioList =
                vistaEmpresaInventarioService.findByInvEmpresaId();

        return vistaEmpresaInventarioList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(vistaEmpresaInventarioList);
    }


    @GetMapping("/empresa_inventario/subseccion/{subSeccionId}")
    public ResponseEntity<List<VistaEmpresaInventario>> findInventariosPorSubseccion(@PathVariable Long subSeccionId) {

        List<VistaEmpresaInventario> vistaEmpresaInventarioList =
                vistaEmpresaInventarioService.findByEmpresaIdAndSubseccionId(subSeccionId);

        return vistaEmpresaInventarioList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(vistaEmpresaInventarioList);
    }

    @GetMapping("/inventario_producto")
    public ResponseEntity<List<VistaInventarioProducto>> findAllInventarioProductos(){
        List<VistaInventarioProducto> vistaInventarioProductoList =
                vistaInventarioProductoService.findByProEmpresaId();

        return vistaInventarioProductoList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(vistaInventarioProductoList);
    }
}
