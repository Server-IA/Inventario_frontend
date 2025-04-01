package com.coagronet.sede.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;
    private final AuthenticationService authenticationService;
    private final UserEmpresaService userEmpresaService;

    public List<SedeDTO> findAll() {
        return sedeRepository.findByEmpresaId(
                userEmpresaService.getEmpresaFromUser(
                        authenticationService.getAuthenticatedUser()).getId())
                .stream()
                .map(sedeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SedeDTO> findAllAvailable() {
        return sedeRepository.findByEmpresaIdAndEstadoIdNot(
                userEmpresaService.getEmpresaFromUser(
                        authenticationService.getAuthenticatedUser()).getId(),
                2)
                .stream()
                .map(sedeMapper::toDto)
                .collect(Collectors.toList());
    }

    public SedeDTO findById(Long requestedId) {
        return sedeRepository.findByIdAndEmpresaId(
                requestedId,
                userEmpresaService.getEmpresaFromUser(
                        authenticationService.getAuthenticatedUser()).getId())
                .map(sedeMapper::toDto)
                .orElse(null);
    }

    public SedeDTO create(SedeDTO newSedeDTORequest) {
        SedeDTO sedeDTO = new SedeDTO(
                null,
                newSedeDTORequest.getGrupoId(),
                newSedeDTORequest.getTipoSedeId(),
                userEmpresaService.getEmpresaFromUser(
                        authenticationService.getAuthenticatedUser()).getId(),
                newSedeDTORequest.getNombre(),
                newSedeDTORequest.getMunicipioId(),
                newSedeDTORequest.getArea(),
                newSedeDTORequest.getComuna(),
                newSedeDTORequest.getDescripcion(),
                newSedeDTORequest.getEstadoId(),
                newSedeDTORequest.getGeolocalizacion(),
                newSedeDTORequest.getCoordenadas(),
                newSedeDTORequest.getDireccion());
        return sedeMapper.toDto(
                sedeRepository.save(
                        sedeMapper.toEntity(sedeDTO)));
    }

}
