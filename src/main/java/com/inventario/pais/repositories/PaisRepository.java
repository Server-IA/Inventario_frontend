/*=============================================================================
 Nombre del archivo : PaisRepository.java
 Descripcion        : Repositorio JPA para consultas de paises.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.inventario.pais.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.pais.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

	List<Pais> findAllByOrderByIdAsc();

	List<Pais> findByEstadoIdNotOrderByIdAsc(Long estadoId);

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

	boolean existsByCodigo(Long codigo);

	boolean existsByCodigoAndIdNot(Long codigo, Long id);

	boolean existsByAcronimoIgnoreCase(String acronimo);

	boolean existsByAcronimoIgnoreCaseAndIdNot(String acronimo, Long id);

}









