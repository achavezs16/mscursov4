package com.mscursosv3.mscursosv3.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

    @Column(nullable = false, length=30)
    private String nombreCurso;

    @Column(nullable = false, length=250)
    private String descCurso;
    
    @Column(nullable = false, length=3)
    private int cantMaxParticipantes;
    
    @Column(nullable = false)
    private Boolean estadoCurso;

    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
}
