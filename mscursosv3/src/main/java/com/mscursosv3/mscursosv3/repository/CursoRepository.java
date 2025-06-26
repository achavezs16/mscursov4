package com.mscursosv3.mscursosv3.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mscursosv3.mscursosv3.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{
    
    Optional<List<Curso>> findByEstadoCurso(Boolean estadoCurso);
    public List<Curso> findByNombreCurso(String nombreCurso);
    public List<Curso> findByFechaCreacion(LocalDate fechaCreacion);
    


}

