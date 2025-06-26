package com.mscursosv3.mscursosv3.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CursoDTO {
    private Long idCurso;
    private String nombreCurso;
    private String descCurso;
    private int cantMaxParticipantes;
    private Boolean estadoCurso;
    private LocalDate fechaCreacion;
}

