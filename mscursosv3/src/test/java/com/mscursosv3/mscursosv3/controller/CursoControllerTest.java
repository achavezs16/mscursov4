package com.mscursosv3.mscursosv3.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.exception.CursoNoEncontradoException;
import com.mscursosv3.mscursosv3.exception.GlobalExceptionHandler;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.service.CursoService;

@WebMvcTest(CursoController.class)
@Import(GlobalExceptionHandler.class)
public class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursoService;

    @Test
    void debeRetornarMensajeStatus() throws Exception{
        mockMvc.perform(get("/api/v2/cursos/status"))
            .andExpect(status().isOk())
            .andExpect(content().string("Gestión de Curso-API está conectado! 🙌"))
            .andDo(print());
    }


    @Test
    void inscribirCurso_DeberiaRetornar201() throws Exception{

        //Given
        Curso curso1 = new Curso();
        curso1.setNombreCurso("Desarrollo FullStack I");
        curso1.setDescCurso("Nivel Principiante");
        curso1.setCantMaxParticipantes(50);
        curso1.setEstadoCurso(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(curso1);

        when(cursoService.crearCurso(any(Curso.class))).thenReturn(curso1);

        //Then
        mockMvc.perform(post("/api/v2/cursos/creacionCurso1")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());     
    }

    @Test
    void buscarCurso_DeberiaRetornar200() throws Exception{

        //Given
        Long idCurso = 1L;
        CursoDTO cursoDTO = new CursoDTO();
        cursoDTO.setIdCurso(idCurso);
        cursoDTO.setNombreCurso("Curso de Prueba");

        when(cursoService.buscarCursoPorId(idCurso)).thenReturn(cursoDTO);

        //When / then
        mockMvc.perform(get("/api/v2/cursos/{idCurso}", idCurso)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void buscarCurso_CursoNOEXISTE_DeberiaRetornarNullYMensajeError() throws Exception{

        //Given
        Long idCursoInexistente = 999L;

        when(cursoService.buscarCursoPorId(idCursoInexistente))
            .thenThrow(new CursoNoEncontradoException("Curso consultado no encontrado: " + idCursoInexistente));


        mockMvc.perform(get("/api/v2/cursos/{idCurso}", idCursoInexistente)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detalle").value("Curso consultado no encontrado: " + idCursoInexistente))
            .andDo(print());
    }

    @Test
    void listarCursos_FullLista_DeberiaRetornarDatos() throws Exception{

        String URI = "/api/v2/cursos/listarCursos1";

        CursoDTO cursoDTO1 = new CursoDTO();
        cursoDTO1.setNombreCurso("Desarrollo Fullstack I");
        cursoDTO1.setDescCurso("Nivel Principiante");
        cursoDTO1.setCantMaxParticipantes(50);
        cursoDTO1.setEstadoCurso(true);

        CursoDTO cursoDTO2 = new CursoDTO();
        cursoDTO2.setNombreCurso("Desarrollo Fullstack II");
        cursoDTO2.setDescCurso("Nivel Intermedio");
        cursoDTO2.setCantMaxParticipantes(70);
        cursoDTO2.setEstadoCurso(true);        

        CursoDTO cursoDTO3 = new CursoDTO();
        cursoDTO3.setNombreCurso("Taller de Proyectos Fullstack");
        cursoDTO3.setDescCurso("Nivel Avanzado");
        cursoDTO3.setCantMaxParticipantes(30);
        cursoDTO3.setEstadoCurso(false);  

        List<CursoDTO> cursoDTOLista = List.of(cursoDTO1, cursoDTO2, cursoDTO3);

        when(cursoService.listarTodosCursos()).thenReturn(cursoDTOLista);

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCurso").value("Desarrollo Fullstack I"))
                .andExpect(jsonPath("$[1].nombreCurso").value("Desarrollo Fullstack II"))
                .andExpect(jsonPath("$[2].nombreCurso").value("Taller de Proyectos Fullstack"))
                .andDo(print());

    }

    @Test
    void listarCurso_ListaVacia_DeberiaRetornar204SinContenido() throws Exception {
        String URI = "/api/v2/cursos/listarCursos1";

        when(cursoService.listarTodosCursos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URI))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andDo(print());

    }

    @Test
    void modificarCurso_Exitoso_DeberiaRetornar200YCursoActualizado() throws Exception {
        Long idCurso = 1L;
        Curso cursoEnviado = new Curso();
        cursoEnviado.setNombreCurso("Curso Modificado");
        cursoEnviado.setDescCurso("Actualizado");
        cursoEnviado.setCantMaxParticipantes(100);
        cursoEnviado.setEstadoCurso(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(cursoEnviado);

        when(cursoService.actualizarCurso(idCurso, cursoEnviado)).thenReturn(cursoEnviado);

        mockMvc.perform(put("/api/v2/cursos/{idCurso}", idCurso)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreCurso").value("Curso Modificado"))
            .andDo(print());

    }

    @Test
    void modificarCurso_CursoNoExiste_DeberiaRetornar404() throws Exception {
        Long idCurso = 999L;
        Curso cursoEnviado = new Curso();
        cursoEnviado.setNombreCurso("Curso Inexistente");
        cursoEnviado.setDescCurso("No existe");
        cursoEnviado.setCantMaxParticipantes(40);
        cursoEnviado.setEstadoCurso(true);

        String body = new ObjectMapper().writeValueAsString(cursoEnviado);

        when(cursoService.actualizarCurso(idCurso, cursoEnviado)).thenReturn(null);

        mockMvc.perform(put("/api/v2/cursos/{idCurso}", idCurso)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound())
            .andExpect(content().string("CURSO CON ID " + idCurso + " NO ENCONTRADO."))
            .andDo(print());

    }

    @Test
    void modificarCurso_DatosInvalidos_DeberiaRetornar400() throws Exception {
        Long idCurso = 1L;
        Curso cursoInvalido = new Curso();
        cursoInvalido.setNombreCurso("");
        cursoInvalido.setDescCurso("");
        cursoInvalido.setCantMaxParticipantes(0);
        cursoInvalido.setEstadoCurso(null);

        String body = new ObjectMapper().writeValueAsString(cursoInvalido);

        mockMvc.perform(put("/api/v2/cursos/{idCurso}", idCurso)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("ERRORES EN LOS DATOS")))
            .andDo(print());
    }

}
