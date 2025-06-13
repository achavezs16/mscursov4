package com.mscursosv3.mscursosv3.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.exception.CursoNoEncontradoException;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.repository.CursoRepository;
import com.mscursosv3.mscursosv3.service.CursoService;

@WebMvcTest(CursoController.class)
public class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CursoService cursoService;

    @MockitoBean
    private CursoRepository cursoRepository;

    @Test
    void inscribirCurso_DeberiaRetornar200() throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();

        //Given
        Curso curso = new Curso();

        String body = objectMapper.writeValueAsString(curso);

        when(cursoService.crearCurso(curso)).thenReturn(new Curso());

        //Then
        mockMvc.perform(post("/api/v2/cursos/creacionCurso")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());      
    }

    @Test
    void buscarCurso_DeberiaRetornar200() throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();

        //Given
        CursoDTO cursoDTO = new CursoDTO();
        String body = objectMapper.writeValueAsString(cursoDTO);

        when(cursoService.buscarCursoPorId(null)).thenReturn(new CursoDTO());

        //Then
        mockMvc.perform(get("/api/v2/cursos/{idCurso}")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    void buscarCurso_CursoNOEXISTE_DeberiaRetornarNullYMensajeError() throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();

        //Given
        CursoDTO cursoDTO = new CursoDTO();
        CursoNoEncontradoException cursoNoEncontradoException = new CursoNoEncontradoException("Curso buscado no existe: " + cursoDTO.getClass());

        String body = objectMapper.writeValueAsString(cursoDTO);

        when(cursoService.buscarCursoPorId(null)).thenThrow(cursoNoEncontradoException);

        //Then
        mockMvc.perform(get("/api/v2/cursos/{idCurso}")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$detalle").value("Curso consultado no encontrado: null"));
        

    }

}
