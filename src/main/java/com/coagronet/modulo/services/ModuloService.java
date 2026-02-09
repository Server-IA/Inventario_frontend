package com.coagronet.modulo.services;

import org.springframework.stereotype.Service;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.subsistema.repositories.SubSistemaRepository;
import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipoaplicacion.repositories.TipoAplicacionRepository;
import com.coagronet.tipomodulo.TipoModulo;
import com.coagronet.tipomodulo.repositories.TipoModuloRepository;

@Service
public class ModuloService {

        private final ModuloRepository moduloRepository;
        private final EstadoRepository estadoRepository;
        private final SubSistemaRepository subSistemaRepository;
        private final TipoModuloRepository tipoModuloRepository;
        private final TipoAplicacionRepository tipoAplicacionRepository;

        public ModuloService(ModuloRepository moduloRepository, EstadoRepository estadoRepository,
                        SubSistemaRepository subSistemaRepository, TipoModuloRepository tipoModuloRepository,
                        TipoAplicacionRepository tipoAplicacionRepository) {
                this.moduloRepository = moduloRepository;
                this.estadoRepository = estadoRepository;
                this.subSistemaRepository = subSistemaRepository;
                this.tipoModuloRepository = tipoModuloRepository;
                this.tipoAplicacionRepository = tipoAplicacionRepository;
        }

        public Long crearModulo(ModuloRequest request) {

                if (moduloRepository.existsByNombre(request.nombre())) {
                        // Lanza excepción con código "modulo.duplicado"
                        throw new RecursoDuplicadoException(request.nombre());
                }

                Estado estado = estadoRepository.findById(request.estadoId())
                                // Lanza excepción con código "entidad.no_encontrada", params: "Estado", id
                                .orElseThrow(() -> new EntidadNoEncontradaException("Estado", request.estadoId()));

                SubSistema subSistema = subSistemaRepository.findById(request.subSistemaId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("SubSistema",
                                                request.subSistemaId()));

                TipoModulo tipoModulo = tipoModuloRepository.findById(request.tipoModuloId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Tipo de Módulo",
                                                request.tipoModuloId()));

                TipoAplicacion tipoAplicacion = tipoAplicacionRepository.findById(request.tipoAplicacionId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Tipo de Aplicación",
                                                request.tipoAplicacionId()));

                String[] rolesArray = (request.roles() != null && !request.roles().isEmpty())
                                ? request.roles().toArray(new String[0])
                                : null;

                Modulo modulo = Modulo.builder()
                                .nombre(request.nombre())
                                .url(request.url())
                                .descripcion(request.descripcion())
                                .icon(request.icon())
                                .estado(estado)
                                .subSistema(subSistema)
                                .tipoModulo(tipoModulo)
                                .tipoAplicacion(tipoAplicacion)
                                .rolId(rolesArray)
                                .nombreId(request.nombreId())
                                .requerido(request.requerido())
                                .build();

                return moduloRepository.save(modulo).getId();
        }

}
