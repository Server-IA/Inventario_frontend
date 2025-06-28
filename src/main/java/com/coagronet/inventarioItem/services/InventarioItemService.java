package com.coagronet.inventarioItem.services;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.inventarioItem.mappers.InventarioItemMapper;
import com.coagronet.inventarioItem.repositories.InventarioItemRepository;
import com.coagronet.inventarioItem.InventarioItem;
import com.coagronet.inventarioItem.dtos.InventarioItemDTO;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioItemService {

    private final InventarioItemRepository inventarioItemRepository;
    private final EstadoRepository estadoRepository;
    private final InventarioItemMapper inventarioItemMapper;
    private final UserEmpresaService userEmpresaService;
    private final ArticuloKardexRepository articuloKardexRepository;


    public List<InventarioItemDTO> findAll(){

        return inventarioItemRepository.findByEmpresaIdOrderByIdAsc(
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream().map(inventarioItemMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<InventarioItemDTO> findById(Long requestedId){

        return inventarioItemRepository.findByIdAndEmpresaId(requestedId,
                userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(inventarioItemMapper::toDTO);
    }


    @Transactional
    public InventarioItemDTO create(InventarioItemDTO inventarioItemDTO){

        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        estadoRepository.findById(inventarioItemDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("Estado no encontrado o no válido"));

        ArticuloKardex articuloKardex = articuloKardexRepository
                .findByProductoIdentificador(inventarioItemDTO.getProductoIdentificadorId())
                .orElseThrow(() -> new BadRequestException("ArticuloKardex no encontrado para ese identificador"));


        inventarioItemDTO.setEmpresaId(empresaId);

        InventarioItem inventarioItem = inventarioItemMapper.toEntity(inventarioItemDTO);
        inventarioItem.setArticuloKardex(articuloKardex);
        inventarioItem = inventarioItemRepository.save(inventarioItem);
        return inventarioItemMapper.toDTO(inventarioItem);
    }

    @Transactional
    public void update(Long requestedId, InventarioItemDTO inventarioItemDTO){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("InventarioItem no encontrada en su empresa"));

        estadoRepository.findById(inventarioItemDTO.getEstadoId())
                .orElseThrow(()-> new BadRequestException("El estado no es válido"));

        inventarioItemDTO.setId(requestedId);
        inventarioItemDTO.setEmpresaId(empresaId);
        inventarioItemRepository.save(inventarioItemMapper.toEntity(inventarioItemDTO));
    }


    @Transactional
    public void delete(Long requestedId){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        inventarioItemRepository.findByIdAndEmpresaId(requestedId, empresaId)
                .orElseThrow(()-> new NotFoundException("InventarioItem no encontrada en su empresa"));

        inventarioItemRepository.deleteById(requestedId);
    }
}
