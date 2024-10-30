package com.coagronet.produccion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.dtos.DTOProduccion;
import com.coagronet.produccion.mappers.ProduccionMapper;
import com.coagronet.produccion.repositories.ProduccionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProduccionService {

    @Autowired
    private ProduccionRepository produccionRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    public List<Produccion> obtenerProduccionPorEspaciosShort(Integer espacioId, Long empresaId) {
        return produccionRepository.buscarProduccionPorEspacioShort(espacioId, empresaId);
    }

    public Page<Produccion> obtenerProduccionPorEspaciosLong(Integer espacioId, Long empresaId, Pageable paginacion) {
        return produccionRepository.buscarProduccionPorEspacioLong(espacioId, empresaId, paginacion);
    }

    public Produccion guardarProduccion(DTOProduccion dtoProduccion) {
        Produccion produccion = ProduccionMapper.INSTANCE.toEntity(dtoProduccion);
        return produccionRepository.save(produccion);
    }

    public Produccion actualizarProduccion(DTOProduccion dtoProduccion) {
        Produccion produccion = ProduccionMapper.INSTANCE.toEntity(dtoProduccion);
        if (!produccionRepository.existsById(produccion.getId())) {
            throw new EntityNotFoundException("Producción no encontrada");
        }
        return produccionRepository.save(produccion);
    }

    public void eliminarProduccion(Integer id) {
        Produccion produccion = produccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produccion not found with id: " + id));

        Estado nuevoEstado = estadoRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Estado not found with id: 2"));

        produccion.setEstado(nuevoEstado);
        produccionRepository.save(produccion);
    }

}
