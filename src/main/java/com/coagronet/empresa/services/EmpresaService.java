package com.coagronet.empresa.services;

import com.coagronet.utils.Constantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Page<Empresa> getAllEmpresas(Pageable pageable) {
        return empresaRepository.findByEstadoNot(2, pageable);
    }

    public Empresa getEmpresaById(Long id) {
        // Asegúrate de que también se filtren por estado aquí si es necesario
        Empresa empresa = empresaRepository.findById(id).orElse(null);

        return (empresa != null && empresa.getEstado().getId() != 2) ? empresa : null;
    }

    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa update(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public void deleteEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona not found with id: " + id));
        empresa.getEstado().setId(Constantes.ESTADO_INACTIVO);
        empresaRepository.save(empresa);
    }
}
