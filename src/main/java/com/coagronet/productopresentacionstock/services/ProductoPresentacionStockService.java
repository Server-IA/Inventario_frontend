package com.coagronet.productopresentacionstock.services;

import com.coagronet.exceptionHandler.ResourceNotFoundException;
import com.coagronet.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import com.coagronet.productopresentacionstock.mappers.ProductoPresentacionStockMapper;
import com.coagronet.productopresentacionstock.repositories.ProductoPresentacionStockRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductoPresentacionStockService {

    private final ProductoPresentacionStockRepository productoPresentacionStockRepository;
    private final UserEmpresaService userEmpresaService;
    private final ProductoPresentacionStockMapper productoPresentacionStockMapper;




    public Page<ProductoPresentacionStockResponseDTO>findAll(Pageable pageable){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return productoPresentacionStockRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
                .map(productoPresentacionStockMapper::toResponseDTO);
    }

    public ProductoPresentacionStockResponseDTO findByProductoPresentacionId(Long productoPresentacionId){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return productoPresentacionStockRepository.findByEmpresaIdAndProductoPresentacionId(empresaId, productoPresentacionId)
                .map(productoPresentacionStockMapper::toResponseDTO).orElseThrow(() -> new ResourceNotFoundException(
                        "No existe stock para el productoPresentacionId: " + productoPresentacionId));
    }
}
