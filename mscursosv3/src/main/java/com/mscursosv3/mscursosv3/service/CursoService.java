package com.mscursosv3.mscursosv3.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.dto.CursoToCursoDTOConverter;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {
 
    private final CursoRepository cursoRepository;
    private final CursoToCursoDTOConverter cursoToCursoDTOConverter;

    public CursoDTO buscarCursoPorId(Long idCurso){
        return cursoRepository.findById(idCurso)
                .map(cursoToCursoDTOConverter::convert)
                .orElse(null);
    }

    public List<CursoDTO> listarTodosCursos(){
        List<Curso> listaCurso = cursoRepository.findAll();
        List<CursoDTO> cursoDTOs = new ArrayList<>();

        for (Curso curso : listaCurso) {
            CursoDTO dto = cursoToCursoDTOConverter.convert(curso);
            cursoDTOs.add(dto);
        }
        return cursoDTOs;
    }

    public Curso crearCurso(Curso curso){
        return cursoRepository.save(curso);
    }

    public Curso actualizarCurso(Long idCurso, Curso curso){
        if (cursoRepository.existsById(idCurso)) {
            curso.setIdCurso(idCurso);
            return cursoRepository.save(curso);
        }
        return null;
    }

    public Curso obtenerCursoPorId(Long idCurso){
        return cursoRepository.findById(idCurso).orElse(null);
    }

    public void eliminarPorIdCurso(Long idCurso){
        cursoRepository.deleteById(idCurso);
    }

    public List<CursoDTO> estadoCursos(Boolean estadoCurso){
        List<Curso> listaCurso = cursoRepository.findByEstadoCurso(estadoCurso)
                .orElse(Collections.emptyList());
        List<CursoDTO> cursoDTOs = new ArrayList<>();

        for (Curso curso : listaCurso) {
            cursoDTOs.add(cursoToCursoDTOConverter.convert(curso));
        }
        return cursoDTOs;
    }

    public List<CursoDTO> buscarPorNombre(String nombreCurso){
        List<Curso> listaCurso = cursoRepository.findByNombreCurso(nombreCurso);
        List<CursoDTO> cursoDTOs = new ArrayList<>();

        for (Curso curso : listaCurso) {
            cursoDTOs.add(cursoToCursoDTOConverter.convert(curso));
        }
        return cursoDTOs;
    }

    public List<CursoDTO> cursosCreadosDesde(Date fechaCreacion){
        List<Curso> listaCurso = cursoRepository.findByFechaCreacion(fechaCreacion);
        List<CursoDTO> cursoDTOs = new ArrayList<>();

        for (Curso curso : listaCurso) {
            cursoDTOs.add(cursoToCursoDTOConverter.convert(curso));
        }
        return cursoDTOs;
    }



}