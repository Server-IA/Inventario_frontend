package com.coagronet.tipoProduccion.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipoProduccion.repositories.TipoProduccionRepository;

@RestController
@RequestMapping("/api/v1/tipoProduccion")
@CrossOrigin(origins = "*")
public class TipoProduccionController {

    private final TipoProduccionRepository tipoProduccionRepository;

    public TipoProduccionController(TipoProduccionRepository tipoProduccionRepository) {
        this.tipoProduccionRepository = tipoProduccionRepository;
    }

    // Obtener todos los tipos de producción sin paginación
    @GetMapping
    public ResponseEntity<List<TipoProduccion>> listarTiposProduccion() {
        List<TipoProduccion> tiposProduccion = tipoProduccionRepository.listarTipoProduccion();
        return new ResponseEntity<>(tiposProduccion, HttpStatus.OK);
    }

    // Obtener tipo de producción por ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoProduccion> obtenerTipoProduccion(@PathVariable Integer id) {
        return tipoProduccionRepository.findByIdAndEstadoNot(id, 2)
                .map(tipoProduccion -> new ResponseEntity<>(tipoProduccion, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Crear un nuevo tipo de producción
    @PostMapping
    public ResponseEntity<TipoProduccion> crearTipoProduccion(@RequestBody TipoProduccion tipoProduccion) {
        TipoProduccion nuevoTipoProduccion = tipoProduccionRepository.save(tipoProduccion);
        return new ResponseEntity<>(nuevoTipoProduccion, HttpStatus.CREATED);
    }

    // Actualizar un tipo de producción existente
    @PutMapping("/{id}")
    public ResponseEntity<TipoProduccion> actualizarTipoProduccion(@PathVariable Integer id,
            @RequestBody TipoProduccion tipoProduccion) {
        return tipoProduccionRepository.findById(id)
                .map(tipoExistente -> {
                    tipoExistente.setNombre(tipoProduccion.getNombre());
                    tipoExistente.setDescripcion(tipoProduccion.getDescripcion());
                    tipoExistente.setEstado(tipoProduccion.getEstado());
                    TipoProduccion actualizado = tipoProduccionRepository.save(tipoExistente);
                    return new ResponseEntity<>(actualizado, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Eliminar un tipo de producción (cambio de estado a inactivo, por ejemplo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoProduccion(@PathVariable Integer id) {
        return tipoProduccionRepository.findById(id)
                .map(tipoProduccion -> {
                    tipoProduccion.setEstado(2); // Suponiendo que el estado "2" corresponde a inactivo
                    tipoProduccionRepository.save(tipoProduccion);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
