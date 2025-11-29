package com.coagronet.cierreinventariodetalle.services;

import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.cierreinventariodetalle.CierreInventarioDetalle;
import com.coagronet.cierreinventariodetalle.dtos.CierreInventarioDetalleResponseDTO;
import com.coagronet.cierreinventariodetalle.mappers.CierreInventarioDetalleMapper;
import com.coagronet.cierreinventariodetalle.repositories.CierreInventarioDetalleRepository;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CierreInventarioDetalleService {

    private final CierreInventarioDetalleRepository cierreInventarioDetalleRepository;
    private final CierreInventarioDetalleMapper cierreInventarioDetalleMapper;
    private final UserEmpresaService userEmpresaService;
    private final EntidadValidatorFacade entidadValidatorFacade;


    public Page<CierreInventarioDetalleResponseDTO> findAll(Pageable pageable){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        return cierreInventarioDetalleRepository.findByEmpresaId(empresaId, pageable)
                .map(cierreInventarioDetalleMapper::toResponseDTO);
    }


    public CierreInventarioDetalleResponseDTO findById(Long id){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        CierreInventarioDetalle cierreInventarioDetalle =  entidadValidatorFacade.
                validarDetalleCierreInventario(id, empresaId);

        return cierreInventarioDetalleMapper.toResponseDTO(cierreInventarioDetalle);
    }



    @Transactional
    public void generarDetalles(CierreInventario cierreInventario){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        Long almacenId = cierreInventario.getAlmacen().getId();
        LocalDate fechaInicio = cierreInventario.getFechaInicio();
        LocalDate fechaFin = cierreInventario.getFechaCorte();

        LocalDateTime fechaInicioDT = fechaInicio.atStartOfDay();
        LocalDateTime fechaCorteDT = fechaFin.atTime(LocalTime.MAX);

        List<PresentacionProducto> productosPresentacion =
                cierreInventarioDetalleRepository.findPresentacionesEnRango(
                        almacenId, fechaInicioDT, fechaCorteDT
                );

        List<CierreInventarioDetalle> detalles = new ArrayList<>();

        for(PresentacionProducto prp : productosPresentacion){
            BigDecimal stock = cierreInventarioDetalleRepository.calcularStock(prp.getId(), almacenId, empresaId, fechaFin);

            detalles.add(
                    CierreInventarioDetalle.builder()
                            .cierreInventario(cierreInventario)
                            .presentacionProducto(prp)
                            .stock(stock != null ? stock : BigDecimal.ZERO)
                            .fechaCorte(fechaFin)
                            .empresa(cierreInventario.getEmpresa())
                            .almacen(cierreInventario.getAlmacen())
                            .build()
            );


        }
        cierreInventarioDetalleRepository.saveAll(detalles);



    }
}
