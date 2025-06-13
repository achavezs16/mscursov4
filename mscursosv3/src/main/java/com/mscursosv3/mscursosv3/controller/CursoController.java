package com.mscursosv3.mscursosv3.controller;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.service.CursoService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/v2/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/status")
    public String getStatus() {
        return "Gestión de Curso-API está conectado! 🙌";
    }

    @GetMapping("/listarCursos")
    public List<CursoDTO> listarCursos() {
        return cursoService.listarTodosCursos();
    }

    @GetMapping("/{idCurso}")
    public CursoDTO buscarCursoPorId(@PathVariable Long idCurso) {
        return cursoService.buscarCursoPorId(idCurso);
    }
    
    @PostMapping("/creacionCurso")
    public Curso creacionCurso(@RequestBody Curso curso) {        
        return cursoService.crearCurso(curso);
    }

    @PutMapping("/{idCurso}")
    public Curso modificarCurso(@PathVariable Long idCurso, @RequestBody Curso curso) {
        return cursoService.actualizarCurso(idCurso, curso);
    }

    @DeleteMapping("{idCurso}")
    public void eliminarCurso(@PathVariable Long idCurso){
        cursoService.eliminarPorIdCurso(idCurso);
    }

    @GetMapping("/estado-cursos")
    public List<CursoDTO> estadoCursos(@RequestParam Boolean estadoCurso) {
        return cursoService.estadoCursos(estadoCurso);
    }

    @GetMapping("/lista-cursos-desde")
    public List<CursoDTO> listarCursosDesde(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date fechaCreacion) {
        return cursoService.cursosCreadosDesde(fechaCreacion);
    }
    
}