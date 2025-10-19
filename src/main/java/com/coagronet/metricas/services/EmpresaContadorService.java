package com.coagronet.metricas.services;

import com.coagronet.metricas.constantes.ConstantesMetricas;
import com.coagronet.metricas.dtos.EmpresaTotalResponseDTO;
import com.coagronet.metricas.dtos.EntidadTotalDTO;
import com.coagronet.utils.UserEmpresaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaContadorService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserEmpresaService userEmpresaService;

    /**
     * Devuelve los conteos de registros por entidad asociados a la empresa actual.
     *
     * @param entidades Lista opcional de nombres de entidades a contar.
     *                  Si es nula o vacía, se contarán todas las entidades registradas en ConstantesMetricas.
     * @return DTO con el ID de la empresa y la lista de totales por entidad.
     */

    public EmpresaTotalResponseDTO contarEntidades(List<String> entidades) {
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        List<String> entidadesSolicitadas = (entidades == null || entidades.isEmpty())
                ? new ArrayList<>(ConstantesMetricas.ENTITY_MAP.keySet())
                : entidades;

        List<EntidadTotalDTO> totalesPorEntidad = new ArrayList<>();
        List<String> entidadesInvalidas = new ArrayList<>();

        for (String entidadClave : entidadesSolicitadas) {
            String nombreEntidadJPA = ConstantesMetricas.ENTITY_MAP.get(entidadClave);

            if (nombreEntidadJPA == null) {
                entidadesInvalidas.add(entidadClave);
                continue;
            }

            Long totalRegistros = contarPorEmpresa(nombreEntidadJPA, empresaId);
            totalesPorEntidad.add(new EntidadTotalDTO(entidadClave, totalRegistros));
        }

        return new EmpresaTotalResponseDTO(empresaId, totalesPorEntidad, entidadesInvalidas);
    }

    /**
     * Ejecuta una consulta JPQL para contar los registros de una entidad asociados a la empresa dada.
     */
    private Long contarPorEmpresa(String nombreEntidadJPA, Long empresaId) {
        String jpql = String.format(
                "SELECT COUNT(e) FROM %s e WHERE e.empresa.id = :empresaId",
                nombreEntidadJPA
        );

        return (Long) entityManager.createQuery(jpql)
                .setParameter("empresaId", empresaId)
                .getSingleResult();
    }
}
