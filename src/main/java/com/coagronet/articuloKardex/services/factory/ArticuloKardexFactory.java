package com.coagronet.articuloKardex.services.factory;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.mappers.ArticuloKardexMapper;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloKardexFactory {

    private final ArticuloKardexMapper articuloKardexMapper;
    private final ArticuloKardexRepository articuloKardexRepository;
    private final PresentacionProductoRepository presentacionProductoRepository;
    private final RequestUtils requestUtils;
    private final AuthenticationService authenticationService;

    public List<ArticuloKardex> crearArticulos(
            ArticuloKardexDTO dto,
            Long empresaId,
            HttpServletRequest request) {
        if (esDesgregado(dto, empresaId)) {
            return crearArticulosDesgregados(dto, empresaId, request);
        } else {
            dto.setEmpresaId(empresaId);
            ArticuloKardex entidad = articuloKardexMapper.toEntity(dto);

            asignarDatosAuditoria(entidad, request);

            return List.of(articuloKardexRepository.save(entidad));
        }
    }

    private boolean esDesgregado(ArticuloKardexDTO dto, Long empresaId) {
        return presentacionProductoRepository.findByIdAndEmpresaId(dto.getPresentacionProductoId(), empresaId)
                .map(PresentacionProducto::getDesgregar)
                .orElse(false);
    }

    private List<ArticuloKardex> crearArticulosDesgregados(
            ArticuloKardexDTO dto,
            Long empresaId,
            HttpServletRequest request) {
        double cantidad = dto.getCantidad();
        long unidades = Math.round(cantidad);

        if (Math.abs(cantidad - unidades) > 1e-9) {
            throw new BadRequestException(
                    "Para presentaciones desgregadas, la cantidad debe ser un número entero.");
        }

        List<ArticuloKardex> creados = new ArrayList<>();
        for (int i = 0; i < unidades; i++) {
            ArticuloKardexDTO item = construirArticuloKardexUnitario(dto, empresaId);
            ArticuloKardex entidad = articuloKardexMapper.toEntity(item);

            // ahora recibe el request
            asignarDatosAuditoria(entidad, request);

            creados.add(articuloKardexRepository.save(entidad));
        }
        return creados;
    }

    private void asignarDatosAuditoria(ArticuloKardex entidad, HttpServletRequest request) {

        entidad.setIp(requestUtils.getClientIp(request));
        entidad.setHost(requestUtils.getClientHost(request));

        entidad.setUsername(authenticationService
                .getAuthenticatedUser()
                .getUsername());

        entidad.setRol(requestUtils.getAuthenticatedRole());
    }

    private static ArticuloKardexDTO construirArticuloKardexUnitario(ArticuloKardexDTO articuloKardexDTO,
            Long empresaId) {
        ArticuloKardexDTO item = new ArticuloKardexDTO();
        item.setEmpresaId(empresaId);
        item.setKardexId(articuloKardexDTO.getKardexId());
        item.setPresentacionProductoId(articuloKardexDTO.getPresentacionProductoId());
        item.setEstadoId(articuloKardexDTO.getEstadoId());

        item.setCantidad(1.0);

        item.setPrecio(articuloKardexDTO.getPrecio());
        item.setFechaVencimiento(articuloKardexDTO.getFechaVencimiento());
        item.setIdentificadorProducto(articuloKardexDTO.getIdentificadorProducto());
        item.setLote(articuloKardexDTO.getLote());
        return item;
    }

}
