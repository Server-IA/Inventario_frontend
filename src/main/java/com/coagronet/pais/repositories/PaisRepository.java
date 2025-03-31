package com.coagronet.pais.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.pais.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer>{

}
