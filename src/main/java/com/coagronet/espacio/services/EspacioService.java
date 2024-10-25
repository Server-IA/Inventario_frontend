package com.coagronet.espacio.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coagronet.espacio.Espacio;
import com.coagronet.espacio.repositories.EspacioRepository;

@Service
public class EspacioService {

    @Autowired
    private EspacioRepository espacioRepository;

    public List<Espacio> obtenerEspaciosPorBloque(Integer bloqueId, Long empresaId) {
        return espacioRepository.buscarEspacioPorBloque(bloqueId, empresaId);
    }

}
