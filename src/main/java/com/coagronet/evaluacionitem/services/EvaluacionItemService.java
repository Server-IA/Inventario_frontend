package com.coagronet.evaluacionitem.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemCreateDTO;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemResponseDTO;
import com.coagronet.evaluacionitem.mappers.EvaluacionItemMapper;
import com.coagronet.evaluacionitem.repositories.EvaluacionItemRepository;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionItemService {
    private final EvaluacionItemRepository evaluacionItemRepository;
    private final UserEmpresaService userEmpresaService;
    private final EvaluacionItemMapper evaluacionItemMapper;
    private final EntidadValidatorFacade entidadValidatorFacade;


    public List<EvaluacionItemResponseDTO> findAllByEvaluacionId(Long evaluacionId){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        return evaluacionItemRepository.findByEvaluacionIdAndEmpresaId(evaluacionId, empresaId)
                .stream()
                .map(evaluacionItemMapper::toDto)
                .toList();
    }

    public EvaluacionItemResponseDTO create(EvaluacionItemCreateDTO evaluacionItemCreateDTO){
        Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

        Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
        Evaluacion evaluacion;

        evaluacionItemMapper.toEntity(evaluacionItemCreateDTO);

        return null;
    }


}
