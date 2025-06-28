package com.mscursosv3.mscursosv3.controller;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
import com.mscursosv3.mscursosv3.exception.CursoNoEncontradoException;
import com.mscursosv3.mscursosv3.model.Curso; 
import com.mscursosv3.mscursosv3.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @GetMapping(value = "/listarCursos1", produces = "application/hal+json")
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
    }
    
    @Operation(summary = "Buscar curso por ID", description = "Devuelve curso con sus enlaces HATEOAS")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Curso encontrado", content = @Content(schema = @Schema(implementation = CursoDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")})
    @GetMapping("/{idCurso}")
    public ResponseEntity<?> buscarCursoPorId(@PathVariable Long idCurso) {
        try{
            CursoDTO cursoDTO = cursoService.buscarCursoPorId(idCurso);
            EntityModel<CursoDTO> recurso = cursoAssembler.toModel(cursoDTO);
            return ResponseEntity.ok(recurso);

        } catch (CursoNoEncontradoException ex) {
            throw ex;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR AL BUSCAR CURSO: " + e.getMessage());
        }
    }

    @Operation(summary = "Creacion de curso", description = "Crea un nuevo curso y retorna sus datos junto a enlaces HATEOAS")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Curso creado exitosamente", content = @Content(schema = @Schema(implementation = CursoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos Inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno. Falla en BD")})
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
            CursoDTO cursoDTO = cursoToCursoDTOConverter.convert(cursoCreado);
            EntityModel<CursoDTO> recurso = cursoAssembler.toModel(cursoDTO);

            return ResponseEntity.created(linkTo(methodOn(CursoController.class).buscarCursoPorId(cursoDTO.getIdCurso())).toUri())
                                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Modificar curso por ID", description = "Actualiza datos de curso y retorna actualización junto a enlaces HATEOAS")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Curso Actualizado", content = @Content(schema = @Schema(implementation = CursoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno. Falla en BD")})
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
                CursoDTO cursoDTO = cursoToCursoDTOConverter.convert(cursoActualizado);
                EntityModel<CursoDTO> recurso = cursoAssembler.toModel(cursoDTO);
                return ResponseEntity.ok(recurso);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("CURSO CON ID " + idCurso + " NO ENCONTRADO.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR AL ACTUALIZAR EL CURSO: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar curso por ID", description = "Elimina curso existente y retorna su representación con enlaces HATEOAS")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Curso Eliminado", content = @Content(schema = @Schema(implementation = CursoDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno. Falla en BD")})
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

            EntityModel<CursoDTO> recurso = cursoAssembler.toModel(cursoDTO)
                        .add(linkTo(methodOn(CursoController.class).listarCursos())
                            .withRel("curso"))
                        .add(linkTo(methodOn(CursoController.class).creacionCurso1(null, null))
                            .withRel("create"));

            return ResponseEntity.ok().body(
                    Map.of(
                        "mensaje", "Curso eliminado correctamente.",
                        "cursoEliminado", recurso
                    )   
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR AL ELIMINAR CURSO: " + e.getMessage());
        }
    }
 
    @Operation(summary = "Filtro de cursos activos e inactivos", description = "Retorna todos los cursos activos o inactivos según parámetro entregado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Cursos filtrados por estado", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CursoDTO.class)))),
                    @ApiResponse(responseCode = "204", description = "No hay cursos con el estado consultado"),
                    @ApiResponse(responseCode = "400", description = "Falta el parámetro 'estadoCurso'"),
                    @ApiResponse(responseCode = "500", description = "Error interno. Falla en BD")})
    @GetMapping("/estado-cursos")
    public ResponseEntity<?> obtenerCursosPorEstado(@Parameter(description = "true para cursos activos, false para inactivos", required = true, example = "true")
                                                    @RequestParam(required = false) Boolean estadoCurso) {
        try{
            if (estadoCurso == null){
                return ResponseEntity.badRequest()
                                    .body("Debe indicar true para cursos activos y false para cursos inactivos.");

            }

            List<CursoDTO> cursos = cursoService.estadoCursos(estadoCurso);

            if(cursos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            List<EntityModel<CursoDTO>> recursos = cursos.stream()
                        .map(cursoAssembler::toModel)
                        .toList();

            CollectionModel<EntityModel<CursoDTO>> coleccion = CollectionModel.of(recursos, 
                        linkTo(methodOn(CursoController.class).obtenerCursosPorEstado(estadoCurso)).withSelfRel());

            return ResponseEntity.ok(coleccion);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error inesperado al filtrar cursos por estado: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Lista de cursos creados desde cierta fecha", description = "Devuelve todos los cursos cuya 'fechaCreacion' es igual o posterior al parámetro entregado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Curso encontrados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CursoDTO.class)))),
                    @ApiResponse(responseCode = "204", description = "No hay cursos desde la fecha indicada"),
                    @ApiResponse(responseCode = "400", description = "Parámetro de fecha faltante o con formato incorrecto"),
                    @ApiResponse(responseCode = "500", description = "Error interno. Falla en BD")})
    @GetMapping("/lista-cursos-desde")
    public ResponseEntity<?> listarCursosDesde(@Parameter(description = "Fecha en formato (yyyy-MM-dd). Ej: 2024-01-01", required = true, example = "2024-01-01")
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fechaCreacion) {
        try{
            List<CursoDTO> cursosDTO = cursoService.cursosCreadosDesde(fechaCreacion);

            if(cursosDTO.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            List<EntityModel<CursoDTO>> recursos = cursosDTO.stream()
                    .map(cursoAssembler::toModel)
                    .toList();

            CollectionModel<EntityModel<CursoDTO>> coleccion = CollectionModel.of(recursos, 
                    linkTo(methodOn(CursoController.class).listarCursosDesde(fechaCreacion)).withSelfRel());

            return ResponseEntity.ok(coleccion);

        }   catch (DateTimeParseException ex){
                return ResponseEntity.badRequest().body("Formato de fecha incorrecto. Use yyyy-MM-dd.");
        }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR AL OBTENER CURSOS DESDE: " + e.getMessage());
        }
    }

}