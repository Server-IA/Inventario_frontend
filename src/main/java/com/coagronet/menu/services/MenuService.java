package com.coagronet.menu.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.infrastructure.security.CustomUserDetails;
import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.repositories.MenuModuloRepository;
import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.mappers.ModuloMapper;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.moduloempresa.ModuloEmpresa;
import com.coagronet.moduloempresa.repositories.ModuloEmpresaRepository;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.tipoaplicacion.enums.TipoAplicacionEnum;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio responsable de construir el menú visible para el usuario.
 * <p>
 * Resuelve la empresa y el rol desde el contexto de seguridad, traduce el
 * {@code tipoAplicacion} a {@link TipoAplicacionEnum}, consulta el repositorio y agrupa
 * los módulos por subsistema para producir la estructura final del menú.
 * </p>
 *
 * <p>
 * <strong>Principios:</strong> SRP (construcción del menú), SoC (consulta en repository),
 * y uso de {@link ModuloMapper} para separar el mapeo entidad→DTO.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuModuloRepository menuModuloRepository;
    private final ModuloMapper moduloMapper;
    private final UserEmpresaService userEmpresaService;
    private final ModuloRepository moduloRepository;
    private final ModuloEmpresaRepository moduloEmpresaRepository;
    private final EmpresaRepository empresaRepository;
    private final EstadoRepository estadoRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<MenuSubSistemaResponseDTO> obtenerMenuPorEmpresaTipoYRol(String tipoAplicacion) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        // 1. Manejo del tipo de aplicación (400 Bad Request)
        int tipoAppId;
        try {
            tipoAppId = TipoAplicacionEnum.from(tipoAplicacion).id();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tipo de aplicación proporcionado no es válido: " + tipoAplicacion);
        }

        // 2. Extracción segura del rol desde Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long rolId = null;

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            rolId = userDetails.rolId();
        }

        if (rolId == null) {
            throw new AccessDeniedException("No se pudo extraer el rol del token de seguridad");
        }

        // 3. Validación de permisos basada en la entidad Rol
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado: " + rolId));

        boolean esAdminSistema = "ADMINISTRADOR_SISTEMA".equalsIgnoreCase(rol.getNombre())
                || "ROLE_ADMINISTRADOR_SISTEMA".equalsIgnoreCase(rol.getNombre());
        boolean filtrarAdminEmpresa = !esAdminSistema;

        // 4. Consulta a base de datos
        var rows = menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRolId(
                empresaId, tipoAppId, rolId, filtrarAdminEmpresa);

        record SubSistemaKey(String nombre, String icono) {}

        Map<SubSistemaKey, List<SubModuloRow>> agrupado = rows.stream()
                .collect(Collectors.groupingBy(r -> new SubSistemaKey(r.getSubNombre(), r.getSubIcon()),
                        LinkedHashMap::new, Collectors.toList()));

        List<MenuSubSistemaResponseDTO> out = new ArrayList<>();

        for (var entry : agrupado.entrySet()) {
            SubSistemaKey key = entry.getKey();
            List<MenuModuloResponseDTO> modulos = entry.getValue().stream().map(moduloMapper::toDTO).toList();

            out.add(MenuSubSistemaResponseDTO.builder()
                    .nombre(key.nombre())
                    .icono(key.icono())
                    .modulos(modulos)
                    .build());
        }

        return out;
    }

    @Transactional(readOnly = true)
    public List<MenuSubSistemaResponseDTO> obtenerModulosDisponiblesParaEmpresa() {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        List<Modulo> modulosFaltantes = menuModuloRepository.findModulosNoAsignados(empresaId);

        Map<SubSistema, List<Modulo>> modulosPorSubsistema = modulosFaltantes.stream()
                .collect(Collectors.groupingBy(Modulo::getSubSistema));

        List<MenuSubSistemaResponseDTO> respuesta = new ArrayList<>();

        modulosPorSubsistema.forEach((subsistema, listaModulos) -> {
            List<MenuModuloResponseDTO> modulosDto = listaModulos.stream()
                    .map(m -> new MenuModuloResponseDTO(m.getNombreId(), m.getNombre(), m.getUrl(), m.getIcon()))
                    .toList();

            respuesta.add(new MenuSubSistemaResponseDTO(subsistema.getNombre(), subsistema.getIcon(), modulosDto));
        });

        return respuesta;
    }

    @Transactional
    public void asignarModulosAEmpresa(List<String> modulosIds) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        List<Modulo> modulosSolicitados = moduloRepository.findByNombreIdIn(modulosIds);

        if (modulosSolicitados.isEmpty()) {
            throw new RuntimeException("No se encontraron módulos válidos con los IDs proporcionados");
        }

        Estado estadoActivo = estadoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Estado activo no configurado"));

        // Optimización Batch: Evitamos N+1 Consultas obteniendo todas las relaciones existentes de una vez
        Set<Long> modulosSolicitadosIds = modulosSolicitados.stream()
                .map(Modulo::getId)
                .collect(Collectors.toSet());

        Set<Long> moduloIdsYaAsignados = moduloEmpresaRepository
                .findModuloIdsByEmpresaIdAndModuloIdIn(empresaId, modulosSolicitadosIds);

        List<ModuloEmpresa> nuevasAsignaciones = modulosSolicitados.stream()
                .filter(modulo -> !moduloIdsYaAsignados.contains(modulo.getId()))
                .map(modulo -> {
                    ModuloEmpresa nuevaRelacion = new ModuloEmpresa();
                    nuevaRelacion.setEmpresa(empresa);
                    nuevaRelacion.setModulo(modulo);
                    nuevaRelacion.setEstado(estadoActivo);
                    return nuevaRelacion;
                }).toList();

        // Guardado en lote apoyado en el ciclo de persistencia de Spring Data JPA
        if (!nuevasAsignaciones.isEmpty()) {
            moduloEmpresaRepository.saveAll(nuevasAsignaciones);
        }
    }
}