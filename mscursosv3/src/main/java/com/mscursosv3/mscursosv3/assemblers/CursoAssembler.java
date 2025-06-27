package com.mscursosv3.mscursosv3.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.mscursosv3.mscursosv3.controller.CursoController;
import com.mscursosv3.mscursosv3.dto.CursoDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CursoAssembler implements RepresentationModelAssembler<CursoDTO, EntityModel<CursoDTO>> {

    @Override
    public EntityModel<CursoDTO> toModel(@NonNull CursoDTO cursoDTO) {
        return EntityModel.of(cursoDTO,
            linkTo(methodOn(CursoController.class).buscarCursoPorId(cursoDTO.getIdCurso())).withSelfRel(),
            linkTo(methodOn(CursoController.class).listarCursos()).withRel("cursos"),
            //linkTo(methodOn(CursoController.class).modificarCurso(cursoDTO.getIdCurso(), null)).withRel("update"),
            linkTo(methodOn(CursoController.class).eliminarCurso(cursoDTO.getIdCurso())).withRel("delete")
        
        );

    }

}
