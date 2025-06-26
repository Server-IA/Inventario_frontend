package com.coagronet.tipoBloque.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.tipoBloque.TipoBloque;
import com.coagronet.tipoBloque.dtos.TipoBloqueDTO;
import com.coagronet.tipoBloque.mappers.TipoBloqueMapper;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoBloqueService {
    private final TipoBloqueRepository tipoBloqueRepository;
    private final EstadoRepository estadoRepository;
    private final TipoBloqueMapper tipoBloqueMapper;
    private final UserEmpresaService userEmpresaService;

    public Optional<TipoBloqueDTO> findById(Long requestedId) {
        return tipoBloqueRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest(), 2)
                .map(tipoBloqueMapper::toDTO);
    }

    public List<TipoBloqueDTO> findAll() {
        return tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), 2)
                .stream()
                .map(tipoBloqueMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TipoBloqueDTO> findAllMinimal() {
        return tipoBloqueRepository
                .findByEmpresaIdAndEstadoIdNotOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), 2)
                .stream()
                .map(tipoBloqueMapper::toMinimalDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TipoBloqueDTO create(TipoBloqueDTO tipoBloqueDTO) {
        tipoBloqueDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        return tipoBloqueMapper.toDTO(tipoBloqueRepository.save(tipoBloqueMapper.toEntity(tipoBloqueDTO)));
    }

    @Transactional
    public void update(Long requestedId, TipoBloqueDTO tipoBloqueDTO) {
        tipoBloqueRepository
                .findByIdAndEmpresaIdAndEstadoIdNot(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest(), 2)
                .orElseThrow(() -> new NotFoundException("TipoBloque no encontrado o está inactivo"));

        estadoRepository.findById(tipoBloqueDTO.getEstadoId())
                .orElseThrow(() -> new NotFoundException("Estado no encontrado"));

        tipoBloqueDTO.setId(requestedId);
        tipoBloqueDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

        tipoBloqueRepository.save(tipoBloqueMapper.toEntity(tipoBloqueDTO));
    }

    @Transactional
    public void delete(Long id) {
        TipoBloque tipoBloque = tipoBloqueRepository
                .findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
                .orElseThrow(() -> new NotFoundException("TipoBloque no encontrado o ya está inactivo"));

        tipoBloqueRepository.deleteById(tipoBloque.getId());

    }

}
