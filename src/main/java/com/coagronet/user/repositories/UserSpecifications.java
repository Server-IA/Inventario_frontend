package com.coagronet.user.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.coagronet.persona.Persona;
import com.coagronet.user.User;
import com.coagronet.user.dtos.UsuarioFiltroRequest;
import com.coagronet.usuariorol.UsuarioRol;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UserSpecifications {

    public static Specification<User> conFiltros(UsuarioFiltroRequest filtro, Long forcedEmpresaId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("persona", JoinType.LEFT);
                root.fetch("preferredRol", JoinType.LEFT);
            }

            Join<User, Persona> personaJoin = root.join("persona", JoinType.INNER);

            boolean needsRoleJoin = forcedEmpresaId != null || filtro.empresaId() != null
                    || filtro.rolId() != null || filtro.estadoId() != null;

            if (needsRoleJoin) {
                Join<User, UsuarioRol> rolesAsignadosJoin = root.join("rolesAsignados", JoinType.INNER);

                if (forcedEmpresaId != null) {
                    predicates.add(cb.equal(rolesAsignadosJoin.get("empresa").get("id"), forcedEmpresaId));
                } else if (filtro.empresaId() != null) {
                    predicates.add(cb.equal(rolesAsignadosJoin.get("empresa").get("id"), filtro.empresaId()));
                }

                if (filtro.rolId() != null) {
                    predicates.add(cb.equal(rolesAsignadosJoin.get("rol").get("id"), filtro.rolId()));
                }

                if (filtro.estadoId() != null) {
                    predicates.add(cb.equal(rolesAsignadosJoin.get("estado").get("id"), filtro.estadoId()));
                }

                query.distinct(true);
            }

            if (StringUtils.hasText(filtro.username())) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + filtro.username().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(filtro.nombre())) {
                predicates.add(cb.like(cb.lower(personaJoin.get("nombre")), "%" + filtro.nombre().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(filtro.apellido())) {
                predicates.add(
                        cb.like(cb.lower(personaJoin.get("apellido")), "%" + filtro.apellido().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}