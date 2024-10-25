package com.coagronet.kardexItem.asemblers;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.coagronet.kardexItem.KardexItem;
import com.coagronet.kardexItem.controllers.KardexItemController;
import com.coagronet.kardexItem.dtos.KardexItemDTO;
import com.coagronet.kardexItem.mappers.KardexItemMapper;

@Component
public class KardexItemModelAssembler implements RepresentationModelAssembler<KardexItem, EntityModel<KardexItemDTO>> {

    private final KardexItemMapper kardexItemMapper;

    public KardexItemModelAssembler(KardexItemMapper kardexItemMapper) {
        this.kardexItemMapper = kardexItemMapper;
    }

    @Override
    public EntityModel<KardexItemDTO> toModel(KardexItem kardexItem) {
        KardexItemDTO kardexItemDTO = kardexItemMapper.toDto(kardexItem);
        return EntityModel.of(kardexItemDTO,
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(KardexItemController.class).findKardexItem(kardexItem.getId()))
                        .withSelfRel(),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(KardexItemController.class).all(2, Pageable.unpaged()))
                        .withRel("kardexItem"));
    }

}
