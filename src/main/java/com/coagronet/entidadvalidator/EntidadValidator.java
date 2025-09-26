package com.coagronet.entidadvalidator;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.entidadvalidator.constantes.EntidadValidatorConstantes;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.repositories.TipoMovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntidadValidator {

    private final EmpresaRepository empresaRepository;
    private final EstadoRepository estadoRepository;
    private final AlmacenRepository almacenRepository;
    private final ProduccionRepository produccionRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final KardexRepository kardexRepository;

    public Empresa validarEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.EMPRESA_NO_ENCONTRADA));
    }

    public Estado validarEstado(Long estadoId) {
        return estadoRepository.findById(estadoId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.ESTADO_NO_VALIDO));
    }

    public Almacen validarAlmacen(Long almacenId, Long empresaId) {
        return almacenRepository.findByIdAndEmpresaId(almacenId, empresaId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.ALMACEN_NO_VALIDO));
    }

    public Produccion validarProduccion(Long produccionId, Long empresaId) {
        return produccionRepository.findByIdAndEmpresaId(produccionId, empresaId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.PRODUCCION_NO_VALIDO));
    }

    public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
        return tipoMovimientoRepository.findByIdAndEmpresaId(tipoMovimientoId, empresaId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.TIPO_MOVIMIENTO_NO_VALIDO));
    }

    public Kardex validarKardex(Long kardexId, Long empresaId) {
        return kardexRepository.findByIdAndEmpresaId(kardexId, empresaId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.KARDEX_NO_VALIDO));
    }

    public Empresa validarClienteProveedor(Long clienteProveedorId) {
        return empresaRepository.findById(clienteProveedorId)
                .orElseThrow(() -> new BadRequestException(EntidadValidatorConstantes.CLIENTE_PROVEEDOR_NO_VALIDO));
    }
}

