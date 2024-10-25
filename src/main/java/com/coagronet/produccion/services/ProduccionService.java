package com.coagronet.produccion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;

@Service
public class ProduccionService {

    @Autowired
    private ProduccionRepository produccionRepository;

    public List<Produccion> obtenerProduccionPorEspacios(Integer espacioId, Long empresaId) {
        return produccionRepository.buscarProduccionPorEspacio(espacioId, empresaId);
    }

}
