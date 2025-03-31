package com.coagronet.municipio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.municipio.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

}