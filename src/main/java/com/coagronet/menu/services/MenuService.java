package com.coagronet.menu.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.infrastructure.security.JwtService;
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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio responsable de construir el menú visible para el usuario.
 * <p>
 * Resuelve la empresa y el rol desde el contexto de seguridad, traduce el {@code tipoAplicacion} a
 * {@link TipoAplicacionEnum}, consulta el repositorio y agrupa los módulos por subsistema para producir la estructura
 * final del menú.
 * </p>
 *
 * <p>
 * <strong>Principios:</strong> SRP (construcción del menú), SoC (consulta en repository), y uso de {@link ModuloMapper}
 * para separar el mapeo entidad→DTO.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Service @RequiredArgsConstructor
public class MenuService {

    private final MenuModuloRepository menuModuloRepository;
    private final ModuloMapper moduloMapper;
    private final UserEmpresaService userEmpresaService;
    private final ModuloRepository moduloRepository;
    private final ModuloEmpresaRepository moduloEmpresaRepository;
    private final EmpresaRepository empresaRepository;
    private final EstadoRepository estadoRepository;
    private final JwtService jwtService;
    private final RolRepository rolRepository;
    private final HttpServletRequest request;

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

        String authHeader = request.getHeader("Authorization");
        Integer rolId = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            rolId = jwtService.extractRoleId(token);
        }

        // 2. Manejo de la falta de Rol (403 Forbidden)
        if (rolId == null) {
            throw new AccessDeniedException("No se pudo extraer el rol del token de seguridad");
        }

        final Integer rolIdFinal = rolId;

        Rol rol = rolRepository.findById(rolIdFinal.longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado: " + rolIdFinal));

        String rolNombre = rol.getNombre();
        boolean esAdminSistema = "ADMINISTRADOR_SISTEMA".equalsIgnoreCase(rolNombre)
                || "ROLE_ADMINISTRADOR_SISTEMA".equalsIgnoreCase(rolNombre);
        boolean filtrarAdminEmpresa = !esAdminSistema;

        var rows = menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRolId(empresaId, tipoAppId, rolIdFinal,
                filtrarAdminEmpresa);

        record SubSistemaKey(String nombre, String icono) {
        }

        Map<SubSistemaKey, List<SubModuloRow>> agrupado = rows.stream().collect(Collectors.groupingBy(
                r -> new SubSistemaKey(r.getSubNombre(), r.getSubIcon()), LinkedHashMap::new, Collectors.toList()));

        List<MenuSubSistemaResponseDTO> out = new ArrayList<>();

        for (var entry : agrupado.entrySet()) {
            SubSistemaKey key = entry.getKey();
            List<MenuModuloResponseDTO> modulos = entry.getValue().stream().map(moduloMapper::toDTO).toList();

            out.add(MenuSubSistemaResponseDTO.builder().nombre(key.nombre()).icono(key.icono()).modulos(modulos)
                    .build());
        }

        return out;
    }

    /**
     * Obtiene y estructura los módulos que aún no han sido asignados a la empresa asociada a la petición actual.
     * <p>
     * El proceso recupera el identificador de la empresa del contexto de seguridad o petición, consulta los módulos no
     * asignados en el repositorio y los agrupa por su {@link SubSistema} correspondiente. Finalmente, transforma las
     * entidades en objetos de transferencia de datos (DTO) para su respuesta.
     * </p>
     *
     * @return Una lista de {@link MenuSubSistemaResponseDTO} que representa la jerarquía de subsistemas y sus
     * respectivos módulos no asignados. Retorna una lista vacía si no existen módulos pendientes.
     * @see #findModulosNoAsignados(Long)
     */
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
                    .collect(Collectors.toList());

            respuesta.add(new MenuSubSistemaResponseDTO(subsistema.getNombre(), subsistema.getIcon(), modulosDto));
        });

        return respuesta;
    }

    @Transactional
    public void asignarModulosAEmpresa(List<String> modulosIds) {

        // 1. Obtener la empresa del contexto actual
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        // 2. Obtener las entidades de los módulos solicitados
        List<Modulo> modulosSolicitados = moduloRepository.findByNombreIdIn(modulosIds);

        if (modulosSolicitados.isEmpty()) {
            throw new RuntimeException("No se encontraron módulos válidos con los IDs proporcionados");
        }

        // 3. Obtener el estado "Activo" (Asumiendo ID 1, o búscalo por nombre)
        Estado estadoActivo = estadoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Estado activo no configurado"));

        // 4. Iterar y guardar SOLO si no existe
        List<ModuloEmpresa> nuevasAsignaciones = new ArrayList<>();

        for (Modulo modulo : modulosSolicitados) {

            // VALIDACIÓN ANTI-DUPLICADOS
            boolean yaExiste = moduloEmpresaRepository.existsByEmpresaAndModulo(empresa, modulo);

            if (!yaExiste) {
                ModuloEmpresa nuevaRelacion = new ModuloEmpresa();
                nuevaRelacion.setEmpresa(empresa);
                nuevaRelacion.setModulo(modulo);
                nuevaRelacion.setEstado(estadoActivo); // moe_estado_id

                nuevasAsignaciones.add(nuevaRelacion);
            }
            // Si ya existe, simplemente lo ignoramos (o podrías lanzar error si prefieres ser estricto)
        }

        // 5. Guardar en lote (Batch save)
        if (!nuevasAsignaciones.isEmpty()) {
            moduloEmpresaRepository.saveAll(nuevasAsignaciones);
        }
    }

}
