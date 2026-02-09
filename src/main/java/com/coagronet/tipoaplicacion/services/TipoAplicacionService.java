package com.coagronet.tipoaplicacion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipoaplicacion.repositories.TipoAplicacionRepository;

@Service
public class TipoAplicacionService {

    private final TipoAplicacionRepository tipoAplicacionRepository;

    public TipoAplicacionService(TipoAplicacionRepository tipoAplicacionRepository) {
        this.tipoAplicacionRepository = tipoAplicacionRepository;
    }

    public List<TipoAplicacion> findAll() {
        return tipoAplicacionRepository.findAll();
    }

}
