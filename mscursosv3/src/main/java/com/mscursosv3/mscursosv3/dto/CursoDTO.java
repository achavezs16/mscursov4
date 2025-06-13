package com.mscursosv3.mscursosv3.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CursoDTO {
    private Long idCurso;
    private String nombreCurso;
    private String descCurso;
    private int cantMaxParticipantes;
    private Boolean cursoActivo;
    private Date fechaCreacion;
}

