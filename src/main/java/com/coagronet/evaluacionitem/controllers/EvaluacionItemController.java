package com.coagronet.evaluacionitem.controllers;

import com.coagronet.evaluacionitem.dtos.EvaluacionItemCreateDTO;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemResponseDTO;
import com.coagronet.evaluacionitem.dtos.EvaluacionItemUpdateDTO;
import com.coagronet.evaluacionitem.services.EvaluacionItemService;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluacion-item")
@RequiredArgsConstructor
public class EvaluacionItemController {

    private final EvaluacionItemService evaluacionItemService;
    private final UriBuilderUtil uriBuilderUtil;

    @GetMapping
    public ResponseEntity<Page<EvaluacionItemResponseDTO>> findAll(@PageableDefault Pageable pageable){
        return ResponseEntity.ok(evaluacionItemService.findAll(pageable));
    }

    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<List<EvaluacionItemResponseDTO>> findAllByEvaluacionId(
            @PathVariable Long evaluacionId) {
        return ResponseEntity.ok(evaluacionItemService.findAllByEvaluacionId(evaluacionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionItemResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluacionItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void>insert(@Valid @RequestBody EvaluacionItemCreateDTO dto, UriComponentsBuilder ucb){

        EvaluacionItemResponseDTO guardado = evaluacionItemService.create(dto);

        URI uri = uriBuilderUtil.buildEvaluacionItemUri(guardado.getId(), ucb);

        return ResponseEntity.created(uri).build();

    }

    @PutMapping("/{evaItemId}")
    public ResponseEntity<Void>update(@PathVariable Long evaItemId, @Valid @RequestBody EvaluacionItemUpdateDTO dto){
        evaluacionItemService.update(evaItemId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{evaItemId}")
    public ResponseEntity<Void> delete(@PathVariable Long evaItemId){
        evaluacionItemService.delete(evaItemId);
        return ResponseEntity.noContent().build();
    }

}
