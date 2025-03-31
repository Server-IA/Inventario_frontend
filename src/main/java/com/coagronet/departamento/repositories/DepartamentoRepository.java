package com.coagronet.departamento.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.departamento.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {

}
