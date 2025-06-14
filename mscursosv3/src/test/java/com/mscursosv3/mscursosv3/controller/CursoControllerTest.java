package com.mscursosv3.mscursosv3.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.dto.CursoToCursoDTOConverter;
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

    @MockitoBean
    private CursoToCursoDTOConverter cursoDTOConverter;

    @Test
    void inscribirCurso_DeberiaRetornar200() throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();

        //Given
        Curso curso1 = new Curso();
        curso1.setNombreCurso("Desarrollo FullStack I");
        curso1.setDescCurso("Nivel Principiante");
        curso1.setCantMaxParticipantes(50);
        curso1.setEstadoCurso(true);

        String body = objectMapper.writeValueAsString(curso1);

        when(cursoService.crearCurso(curso1)).thenReturn(new Curso());

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

    @Test
    void listarCursos_FullLista_DeberiaRetornarDatos() throws Exception{

        //ObjectMapper objectMapper = new ObjectMapper();

        //Given
        String URI = "/api/v2/cursos/listarCursos";

        //Mock Data
        Curso curso1 = new Curso();
        curso1.setNombreCurso("Desarrollo FullStack I");
        curso1.setDescCurso("Nivel Principiante");
        curso1.setCantMaxParticipantes(50);
        curso1.setEstadoCurso(true);

        Curso curso2 = new Curso();
        curso2.setNombreCurso("Desarrollo FullStack II");
        curso2.setDescCurso("Nivel Intermedio");
        curso2.setCantMaxParticipantes(70);
        curso2.setEstadoCurso(true);

        CursoDTO cursoDTO1 = new CursoDTO();
        cursoDTO1.setNombreCurso("Desarrollo FullStack I DTO");
        cursoDTO1.setDescCurso("Nivel Principiante DTO");
        cursoDTO1.setCantMaxParticipantes(50);
        cursoDTO1.setEstadoCurso(true);

        CursoDTO cursoDTO2 = new CursoDTO();
        cursoDTO2.setNombreCurso("Desarrollo FullStack II DTO");
        cursoDTO2.setDescCurso("Nivel Intermedio DTO");
        cursoDTO2.setCantMaxParticipantes(70);
        cursoDTO2.setEstadoCurso(true);

        List<CursoDTO> cursoDTOLista = new ArrayList<>();
        cursoDTOLista.add(cursoDTO1);
        cursoDTOLista.add(cursoDTO2);

        //Mock services responses
        when(cursoService.listarTodosCursos()).thenReturn(List.of(cursoDTO1));
        when(cursoDTOConverter.convert(curso1)).thenReturn(cursoDTO1);
        when(cursoDTOConverter.convert(curso2)).thenReturn(cursoDTO2);

        //When
        MvcResult response = mockMvc.perform(get(URI)).andReturn();

        int responseStatus = response.getResponse().getStatus();
        String responseBody = response.getResponse().getContentAsString();
        
        assertEquals(HttpStatus.OK.value(), responseStatus, "STATUS SHOULD BE 200");
        assertTrue(responseBody.contains("Desarrollo Fullstack I DTO"), "RESPONSE BODY SHOULD CONTAIN 'DESARROLLO FULLSTACK I DTO'");
        


    }

}
