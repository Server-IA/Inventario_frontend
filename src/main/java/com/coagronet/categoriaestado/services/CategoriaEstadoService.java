package com.coagronet.categoriaestado.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.coagronet.categoriaestado.CategoriaEstado;
import com.coagronet.categoriaestado.repositories.CategoriaEstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@PropertySource("classpath:messages.properties")
@RequiredArgsConstructor
public class CategoriaEstadoService {

	private final CategoriaEstadoRepository repository;

	@Value("${response.status.notfound}")
	private String message;

	public List<CategoriaEstado> findAll() {
		return repository.findAllByOrderByIdAsc();
	}

	public Optional<CategoriaEstado> findById(Long id) {
		return repository.findById(id);
	}

	public CategoriaEstado create(CategoriaEstado categoriaEstado) {
		categoriaEstado.setId(null);
		return repository.save(categoriaEstado);
	}

	public void update(Long id, CategoriaEstado categoriaEstado) {
		repository.findById(id).orElseThrow(() -> new NotFoundException(message));
		categoriaEstado.setId(id);
		repository.save(categoriaEstado);
	}

	public void delete(Long id) {
		repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		repository.deleteById(id);
	}

}
