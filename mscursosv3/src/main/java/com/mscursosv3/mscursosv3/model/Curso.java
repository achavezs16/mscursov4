package com.mscursosv3.mscursosv3.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

@Entity
@Table(name = "curso")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCurso;

    @NotBlank(message = "Nombre Curso es OBLIGATORIO")
    @Size(max = 100, message = "Nombre Curso no puede EXCEDER 100 carácteres")
    @Column(nullable = false, length=100)
    private String nombreCurso;

    @NotBlank(message = "Descripción del curso es OBLIGATORIA")
    @Size(max = 250, message = "Deacripción del curso no puede EXCEDER 250 carácteres")
    @Column(nullable = false, length=250)
    private String descCurso;
    
    @Min(value = 1, message = "Debe haber al menos 1 participante")
    @Max(value = 999, message = "No puede exceder 999 participantes")
    @Column(nullable = false, length=3)
    private int cantMaxParticipantes;
    
    @NotNull(message = "Estado de curso debe ser declarado OBLIGATORIO")
    @Column(nullable = false)
    private Boolean estadoCurso;

    private LocalDate fechaCreacion;
}
