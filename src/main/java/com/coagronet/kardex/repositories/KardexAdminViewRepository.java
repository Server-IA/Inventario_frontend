package com.coagronet.kardex.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.coagronet.kardex.KardexAdminView;

public interface KardexAdminViewRepository
        extends JpaRepository<KardexAdminView, Long>, JpaSpecificationExecutor<KardexAdminView> {

}
