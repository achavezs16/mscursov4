package com.mscursosv3.mscursosv3.dto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mscursosv3.mscursosv3.model.Curso;

@Component
public class CursoToCursoDTOConverter implements Converter<Curso, CursoDTO>{

    @Override
    @Nullable
    public CursoDTO convert(Curso curso){
        CursoDTO dto = new CursoDTO();
        dto.setIdCurso(curso.getIdCurso());
        dto.setNombreCurso(curso.getNombreCurso());
        dto.setDescCurso(curso.getDescCurso());
        dto.setCantMaxParticipantes(curso.getCantMaxParticipantes());
        dto.setEstadoCurso(curso.getEstadoCurso());
        dto.setFechaCreacion(curso.getFechaCreacion());
        return dto;
    }
}
