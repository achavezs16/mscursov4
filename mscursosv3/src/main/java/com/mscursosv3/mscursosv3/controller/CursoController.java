package com.mscursosv3.mscursosv3.controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.mscursosv3.mscursosv3.assemblers.CursoAssembler;
import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.dto.CursoToCursoDTOConverter;
import com.mscursosv3.mscursosv3.model.Curso; 
import com.mscursosv3.mscursosv3.service.CursoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v2/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Operaciones relacionadas con la gestión de Cursos de EduTECH")
public class CursoController {

    private final CursoService cursoService;
    private final CursoToCursoDTOConverter cursoToCursoDTOConverter;
    private final CursoAssembler cursoAssembler;

    @Operation(summary = "Verificar estado de API")
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        String message = "Gestión de Curso-API está conectado! 🙌";
        log.info(message);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Obtiene todos los cursos registrados en sistema", description = "Devuelve listado de cursos activos e inactivos con hipervínculos HATEOAS")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista capturada", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CursoDTO.class)))),
                    @ApiResponse(responseCode = "204", description = "Sin contenidos"), @ApiResponse(responseCode = "500", description = "Error interno")})
    @GetMapping("/listarCursos1")
    public ResponseEntity<CollectionModel<EntityModel<CursoDTO>>> listarCursos() {
        List<CursoDTO> cursosDTO = cursoService.listarTodosCursos();

        if(cursosDTO.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<CursoDTO>> modelos = cursosDTO.stream()
                .map(cursoAssembler::toModel)
                .toList();
        
        CollectionModel<EntityModel<CursoDTO>> cursoColeccion = CollectionModel.of(modelos, 
                linkTo(methodOn(CursoController.class).listarCursos()).withSelfRel());
        return ResponseEntity.ok(cursoColeccion);
                
        
        //try {
            //List<CursoDTO> cursosDTO = cursoService.listarTodosCursos();
            //if (cursosDTO.isEmpty()) {
                //return ResponseEntity.noContent().build();
            //}
            //return ResponseEntity.ok(cursosDTO);
        //} catch (Exception e) {
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    //.body("Error al obtener los cursos: " + e.getMessage());
        //}
    }
    
    @Operation(summary = "Buscar curso por ID")
    @GetMapping("/{idCurso}")
    public ResponseEntity<?> buscarCursoPorId(@PathVariable Long idCurso) {
        CursoDTO cursoDTO = cursoService.buscarCursoPorId(idCurso);
        return ResponseEntity.ok(cursoDTO);
    }

    @Operation(summary = "Creacion de curso")
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
    
    @Operation(summary = "Modificar curso por ID")
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

    @Operation(summary = "Eliminar curso por ID")
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
 
    @Operation(summary = "Filtro de cursos activos e inactivos")
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
    
    @Operation(summary = "Lista de cursos creados desde cierta fecha")
    @GetMapping("/lista-cursos-desde")
    public ResponseEntity<?> listarCursosDesde(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fechaCreacion) {
        try{
            List<CursoDTO> cursosDTO = cursoService.cursosCreadosDesde(fechaCreacion);

            if(cursosDTO.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(cursosDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR AL OBTENER CURSOS DESDE: " + e.getMessage());
        }
    }

}