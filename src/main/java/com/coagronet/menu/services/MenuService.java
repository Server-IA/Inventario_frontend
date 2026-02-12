package com.coagronet.menu.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.repositories.MenuModuloRepository;
import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.mappers.ModuloMapper;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.moduloempresa.ModuloEmpresa;
import com.coagronet.moduloempresa.repositories.ModuloEmpresaRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.tipoaplicacion.enums.TipoAplicacionEnum;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

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
    private final UserRoleService userRoleService;
    private final ModuloRepository moduloRepository;
    private final ModuloEmpresaRepository moduloEmpresaRepository;
    private final EmpresaRepository empresaRepository;
    private final EstadoRepository estadoRepository;

    /**
     * Obtiene el menú para la empresa actual y el rol actual del usuario, filtrado por tipo de aplicación.
     * <p>
     * Pasos:
     * <ol>
     * <li>Resuelve {@code empresaId} y {@code roleName} del contexto.</li>
     * <li>Convierte {@code tipoAplicacion} a {@link TipoAplicacionEnum} y obtiene su ID interno.</li>
     * <li>Consulta {@link MenuRepository#findSubmodulosByEmpresaTipoAppAndRol(Long, Integer, String)}.</li>
     * <li>Agrupa por subsistema (nombre + icono) y mapea cada fila a {@link MenuModuloResponseDTO}.</li>
     * </ol>
     * </p>
     *
     * @param tipoAplicacion cadena {@code "web"} o {@code "movil"} (no sensible a mayúsculas)
     * @return lista de subsistemas, cada uno con sus módulos, en orden estable (por nombre de subsistema y módulo)
     * @throws IllegalArgumentException si {@code tipoAplicacion} no corresponde a un valor soportado
     */
    public List<MenuSubSistemaResponseDTO> obtenerMenuPorEmpresaTipoYRol(String tipoAplicacion) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
        String roleName = userRoleService.getRoleFromCurrentRequest();

        int tipoAppId = TipoAplicacionEnum.from(tipoAplicacion).id();

        var rows = menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRol(empresaId, tipoAppId, roleName);

        Map<String, List<SubModuloRow>> agrupado = rows.stream().collect(Collectors
                .groupingBy(r -> r.getSubNombre() + "||" + r.getSubIcon(), LinkedHashMap::new, Collectors.toList()));

        List<MenuSubSistemaResponseDTO> out = new ArrayList<>();
        for (var e : agrupado.entrySet()) {
            String[] parts = e.getKey().split("\\|\\|", 2);
            String subNombre = parts[0];
            String subIcon = parts.length > 1 ? parts[1] : null;

            List<MenuModuloResponseDTO> modulos = e.getValue().stream().map(moduloMapper::toDTO).toList();

            out.add(MenuSubSistemaResponseDTO.builder().nombre(subNombre).icono(subIcon).modulos(modulos).build());
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
