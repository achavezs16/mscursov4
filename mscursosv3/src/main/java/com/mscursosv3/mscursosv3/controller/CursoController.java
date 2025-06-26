package com.mscursosv3.mscursosv3.controller;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.dto.CursoToCursoDTOConverter;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.service.CursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v2/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;
    private final CursoToCursoDTOConverter cursoToCursoDTOConverter;

    @GetMapping("/status")
    public String getStatus() {
        return "Gestión de Curso-API está conectado! 🙌";
    }

    @GetMapping("/listarCursos1")
    public ResponseEntity<?> listarCursos() {
        try {
            List<CursoDTO> cursosDTO = cursoService.listarTodosCursos();
            if (cursosDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cursosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los cursos: " + e.getMessage());
        }
    }
    

    @GetMapping("/{idCurso}")
    public ResponseEntity<?> buscarCursoPorId(@PathVariable Long idCurso) {
        try {
            CursoDTO cursosDTO = cursoService.buscarCursoPorId(idCurso);
            if (cursosDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Curso con ID " + idCurso + " no encontrado.");
            }
            return ResponseEntity.ok(cursosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR AL BUSCAR EL CURSO: " + e.getMessage());
        }
    }

    @PostMapping("/creacionCurso1")
    public ResponseEntity<?> creacionCurso1(@Valid @RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            String errores = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; "+ msg2)
                    .orElse("Datos inválidos");
            return ResponseEntity.badRequest().body("Errores en los datos: " + errores);
        }

        try {
            Curso cursoCreado = cursoService.crearCurso(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }
    
    @PutMapping("/{idCurso}")
    public ResponseEntity<?> modificarCurso(@PathVariable Long idCurso, @Valid @RequestBody Curso curso, BindingResult result) {
        if(result.hasErrors()){
            String errores = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Datos inválidos");
            return ResponseEntity.badRequest().body("ERRORES EN LOS DATOS: " + errores);
        }
        try {
            Curso cursoActualizado = cursoService.actualizarCurso(idCurso, curso);
            if(cursoActualizado != null){
                return ResponseEntity.ok(cursoActualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("CURSO CON ID " + idCurso + " NO ENCONTRADO.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR AL ACTUALIZAR EL CURSO: " + e.getMessage());
        }
    }

    @DeleteMapping("/{idCurso}")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long idCurso){
        try {
            Curso curso = cursoService.obtenerCursoPorId(idCurso);
            if(curso == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un curso con ID " + idCurso + ".");
            }

            CursoDTO cursoDTO = cursoToCursoDTOConverter.convert(curso);
            cursoService.eliminarPorIdCurso(idCurso);

            return ResponseEntity.ok().body(
                    Map.of(
                        "mensaje", "Curso eliminado correctamente.",
                        "cursoEliminado", cursoDTO
                    )   
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR AL ELIMINAR CURSO: " + e.getMessage());
        }
    }

    //@DeleteMapping("{idCurso}")
    //public void eliminarCurso(@PathVariable Long idCurso){
    //    cursoService.eliminarPorIdCurso(idCurso);
    //}

    //@GetMapping("/estado-cursos")
    //public List<CursoDTO> estadoCursos(@RequestParam Boolean estadoCurso) {
        //return cursoService.estadoCursos(estadoCurso);
    //}

    @GetMapping("/estado-cursos")
    public ResponseEntity<?> obtenerCursosPorEstado(@RequestParam(required = false) Boolean estadoCurso) {
        try{
            if (estadoCurso == null){
                return ResponseEntity.badRequest()
                                    .body("Debe indicar true para cursos activos y false para cursos inactivos.");

            }

            List<CursoDTO> cursos = cursoService.estadoCursos(estadoCurso);

            if(cursos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(cursos);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error inesperado al filtrar cursos por estado: " + e.getMessage());
        }
    }

    @GetMapping("/lista-cursos-desde")
    public List<CursoDTO> listarCursosDesde(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date fechaCreacion) {
        return cursoService.cursosCreadosDesde(fechaCreacion);
    }
    
}