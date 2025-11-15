package com.coagronet.cierreinventario.services;

import com.coagronet.almacen.Almacen;
import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.cierreinventario.dtos.CierreInventarioRequestDTO;
import com.coagronet.cierreinventario.dtos.CierreInventarioResponseDTO;
import com.coagronet.cierreinventario.mappers.CierreInventarioMapper;
import com.coagronet.cierreinventario.repositories.CierreInventarioRepository;
import com.coagronet.cierreinventariodetalle.services.CierreInventarioDetalleService;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CierreInventarioService {

    private final CierreInventarioRepository cierreInventarioRepository;
    private final CierreInventarioMapper cierreInventarioMapper;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
    private final EntidadValidatorFacade entidadValidatorFacade;
    private final CierreInventarioDetalleService cierreInventarioDetalleService;


    public List<CierreInventarioResponseDTO> listAll(){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return cierreInventarioRepository.findByEmpresaId(empresaId).stream()
                .map(cierreInventarioMapper::toDTO).toList();
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
