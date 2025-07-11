package com.coagronet.ingredientePresentacionProducto.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.ingredientePresentacionProducto.dtos.IngredientePresentacionProductoDTO;
import com.coagronet.ingredientePresentacionProducto.mappers.IngredientePresentacionProductoMapper;
import com.coagronet.ingredientePresentacionProducto.repositories.IngredientePresentacionProductoRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ingrediente.repositories.IngredienteRepository;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientePresentacionProductoService {

    private final UserEmpresaService userEmpresaService;
    private final IngredientePresentacionProductoMapper ingredientePresentacionProductoMapper;
    private final IngredientePresentacionProductoRepository ingredientePresentacionProductoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final PresentacionProductoRepository presentacionProductoRepository;
    private final EstadoRepository estadoRepository;

    public List<IngredientePresentacionProductoDTO> findAll() {
        return ingredientePresentacionProductoRepository
                .findByEmpresaIdOrderByIdAsc(
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .stream().map(ingredientePresentacionProductoMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<IngredientePresentacionProductoDTO> findById(Long requestedId) {
        return ingredientePresentacionProductoRepository
                .findByIdAndEmpresaId(requestedId,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .map(ingredientePresentacionProductoMapper::toDTO);
    }

    public IngredientePresentacionProductoDTO create(
            IngredientePresentacionProductoDTO ingredientePresentacionProductoDTO) {
        ingredienteRepository
                .findByIdAndEmpresaId(ingredientePresentacionProductoDTO.getIngredienteId(),
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException("El campo ingredienteId no es válido."));

        presentacionProductoRepository
                .findByIdAndEmpresaId(ingredientePresentacionProductoDTO.getPresentacionProductoId(),
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException(
                        "El campo presentacionProductoId no es válido."));

        estadoRepository.findById(ingredientePresentacionProductoDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

        ingredientePresentacionProductoDTO.setId(null);
        ingredientePresentacionProductoDTO.setEmpresaId(
                userEmpresaService.getEmpresaIdFromCurrentRequest());

        return ingredientePresentacionProductoMapper
                .toDTO(ingredientePresentacionProductoRepository
                        .save(ingredientePresentacionProductoMapper.toEntity(ingredientePresentacionProductoDTO)));
    }

    public void update(Long requestedId, IngredientePresentacionProductoDTO ingredientePresentacionProductoDTO) {
        ingredientePresentacionProductoRepository
                .findByIdAndEmpresaId(requestedId,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException(
                        "El ID solicitado no fue encontrado."));

        ingredienteRepository
                .findByIdAndEmpresaId(ingredientePresentacionProductoDTO.getIngredienteId(),
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException("El campo ingredienteId no es válido."));

        presentacionProductoRepository
                .findByIdAndEmpresaId(ingredientePresentacionProductoDTO.getPresentacionProductoId(),
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new BadRequestException(
                        "El campo presentacionProductoId no es válido."));

        estadoRepository.findById(ingredientePresentacionProductoDTO.getEstadoId())
                .orElseThrow(() -> new BadRequestException("El campo estadoId no es válido."));

        ingredientePresentacionProductoDTO.setId(requestedId);
        ingredientePresentacionProductoDTO.setEmpresaId(
                userEmpresaService.getEmpresaIdFromCurrentRequest());

        ingredientePresentacionProductoRepository
                .save(ingredientePresentacionProductoMapper.toEntity(ingredientePresentacionProductoDTO));
    }

    public void delete(Long id) {
        ingredientePresentacionProductoRepository
                .findByIdAndEmpresaId(id,
                        userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException(
                        "El ID solicitado no fue encontrado."));

        ingredientePresentacionProductoRepository.deleteById(id);
    }

}
