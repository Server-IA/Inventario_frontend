package com.inventario.cierreinventario.services;

import com.inventario.almacen.Almacen;
import com.inventario.auditoria.AuthenticationService;
import com.inventario.cierreinventario.CierreInventario;
import com.inventario.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.inventario.cierreinventario.dtos.CierreInventarioResponseDTO;
import com.inventario.cierreinventario.mappers.CierreInventarioMapper;
import com.inventario.cierreinventario.repositories.CierreInventarioRepository;
import com.inventario.cierreinventariodetalle.services.CierreInventarioDetalleService;
import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.user.User;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;
import com.inventario.validator.parametrizacion.constantes.EstadoConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CierreInventarioService {

    private final CierreInventarioRepository cierreInventarioRepository;
    private final CierreInventarioMapper cierreInventarioMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final CierreInventarioDetalleService cierreInventarioDetalleService;


    public Page<CierreInventarioResponseDTO> findAll(Pageable pageable){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return cierreInventarioRepository.findByEmpresaId(empresaId, pageable)
                .map(cierreInventarioMapper::toDTO);
    }

    public CierreInventarioResponseDTO findById(Long cierreId){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        CierreInventario cierre = entidadValidatorFacade
                .validarCierreInventario(cierreId, empresaId);
        return cierreInventarioMapper.toDTO(cierre);
    }

    @Transactional
    public CierreInventarioResponseDTO create(CierreInventarioRequestDTO dto){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        User usuario = authenticationService.getAuthenticatedUser();
        Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
        Almacen almacen = entidadValidatorFacade.validarAlmacen(dto.getAlmacenId(), empresaId);

        Estado estado = entidadValidatorFacade.validarEstadoParaCierre(EstadoConstantes.ESTADO_CIERRE_CERRADO);
        LocalDate fechaInicio = LocalDate.of(dto.getAnio().intValue(), dto.getMes().intValue(), 1);
        LocalDate fechaCorte = fechaInicio.plusMonths(1).minusDays(1);

        entidadValidatorFacade.validarDuplicadoCierreInventario(empresaId, almacen.getId(), fechaInicio, fechaCorte);

        CierreInventario cierre = CierreInventario.builder()
                .empresa(empresa)
                .almacen(almacen)
                .estado(estado)
                .usuario(usuario)
                .fechaInicio(fechaInicio)
                .fechaCorte(fechaCorte)
                .descripcion("Cierre del mes "+dto.getMes()+ "/"+ dto.getAnio())
                .build();

        cierreInventarioRepository.save(cierre);
        cierreInventarioDetalleService.generarDetalles(cierre);
        return cierreInventarioMapper.toDTO(cierre);
    }
}
