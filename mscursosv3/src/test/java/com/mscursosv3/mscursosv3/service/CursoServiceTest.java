package com.mscursosv3.mscursosv3.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mscursosv3.mscursosv3.dto.CursoDTO;
import com.mscursosv3.mscursosv3.dto.CursoToCursoDTOConverter;
import com.mscursosv3.mscursosv3.exception.CursoNoEncontradoException;
import com.mscursosv3.mscursosv3.model.Curso;
import com.mscursosv3.mscursosv3.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CursoToCursoDTOConverter cursoToCursoDTOConverter;

    @InjectMocks
    private CursoService cursoService;

    @Test
    void buscarCursoPorId_Existe_DeberiaRetornarCursoDTO() {

        Long idCurso = 1L;

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setNombreCurso("Fundamento Programacion");

        CursoDTO cursoDTO = new CursoDTO();
        cursoDTO.setIdCurso(idCurso);
        cursoDTO.setNombreCurso("Desarrollo Orientado Objetos");

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso));
        when(cursoToCursoDTOConverter.convert(curso)).thenReturn(cursoDTO);

        CursoDTO cursoResultado = cursoService.buscarCursoPorId(idCurso);

        System.out.println("Resultado del curso obtenido: " + cursoResultado);

        assertThat(cursoResultado).isEqualTo(cursoDTO);
        verify(cursoRepository).findById(idCurso);
        verify(cursoToCursoDTOConverter).convert(curso);

    }

    @Test
    void buscarCursoPorId_NoExiste_DeberiaLanzarCursoNoEncontrado() {
        Long idCursoInexistente = 99L;
        when(cursoRepository.findById(idCursoInexistente)).thenReturn(Optional.empty());

        System.out.println("Probando búsqueda con ID Inexistente: " + idCursoInexistente);

        assertThatThrownBy(() -> cursoService.buscarCursoPorId(idCursoInexistente))
            .isInstanceOf(CursoNoEncontradoException.class)
            .hasMessageContaining("99");
    }

    @Test
    void listarTodosCursos_DeberiaRetornarListaCursoDTO() {

        Curso curso1 = new Curso();
        curso1.setIdCurso(1L);
        curso1.setNombreCurso("Fundamento Programacion");

        Curso curso2 = new Curso();
        curso2.setIdCurso(2L);
        curso2.setNombreCurso("Desarrollo Orientado Objetos");

        List<Curso> cursos = List.of(curso1, curso2);

        CursoDTO cursoDTO1 = new CursoDTO();
        CursoDTO cursoDTO2 = new CursoDTO();
        cursoDTO1.setIdCurso(1L);
        cursoDTO1.setNombreCurso("Fundamento Programacion");
        cursoDTO2.setIdCurso(2l);
        cursoDTO2.setNombreCurso("Desarrollo Orientado Objetos");

        when(cursoRepository.findAll()).thenReturn(cursos);
        when(cursoToCursoDTOConverter.convert(curso1)).thenReturn(cursoDTO1);
        when(cursoToCursoDTOConverter.convert(curso2)).thenReturn(cursoDTO2);

        //When
        List<CursoDTO> cursoResultado = cursoService.listarTodosCursos();

        //then
        assertThat(cursoResultado).hasSize(2);
        assertThat(cursoResultado).containsExactly(cursoDTO1, cursoDTO2);

        verify(cursoRepository).findAll();
        verify(cursoToCursoDTOConverter).convert(curso1);
        verify(cursoToCursoDTOConverter).convert(curso2);

        System.out.println("Resultado de listarTodosCursos: " + cursoResultado);
    }

    @Test
    void listarTodosCursos_SinDatos_DeberiaRetornarListaVacia() {
        when(cursoRepository.findAll()).thenReturn(Collections.emptyList());

        List<CursoDTO> cursoResultado = cursoService.listarTodosCursos();

        assertThat(cursoResultado).isEmpty();
        verify(cursoRepository).findAll();
        verifyNoInteractions(cursoToCursoDTOConverter);

        System.out.println("Sin Datos -> " + cursoResultado);
    }

    @Test
    void crearCurso_Exitoso_DeberiaGuardarYRetornarCurso() {
        Curso curso1 = new Curso();
        curso1.setNombreCurso("Nivelacion Matematica");

        when(cursoRepository.save(curso1)).thenReturn(curso1);

        //when
        Curso cursoResultado = cursoService.crearCurso(curso1);

        //then
        assertThat(cursoResultado).isSameAs(curso1);
        verify(cursoRepository).save(curso1);

        System.out.println("Curso creado: " + cursoResultado);
    }

    @Test
    void crearCurso_ErrorRepositorio_DeberiaPropagarException() {
        Curso curso1 = new Curso();
        curso1.setNombreCurso("Nivelacion Matematica");

        when(cursoRepository.save(curso1)).thenThrow(new RuntimeException("Falla en BD"));

        //when - then
        assertThatThrownBy(() -> cursoService.crearCurso(curso1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Falla en BD");

        verify(cursoRepository).save(curso1);

        System.out.println("Excepcion capturada correctamente");
    
    }

    @Test
    void actualizarCurso_Existe_DeberiaActualizarYRetornarCurso() {
        Long idCurso = 1L;

        Curso cursoExistente = new Curso();
        cursoExistente.setNombreCurso("Desarrollo Fullstack");

        Curso cursoActualizado = new Curso();
        cursoActualizado.setIdCurso(idCurso);
        cursoActualizado.setNombreCurso("Desarrollo Fullstack I");

        when(cursoRepository.existsById(idCurso)).thenReturn(true);
        when(cursoRepository.save(cursoExistente)).thenReturn(cursoActualizado);

        Curso cursoResultado = cursoService.actualizarCurso(idCurso, cursoExistente);

        assertThat(cursoResultado).isEqualTo(cursoActualizado);
        verify(cursoRepository).existsById(idCurso);
        verify(cursoRepository).save(cursoExistente);

        System.out.println("Curso Actualizado: " + cursoResultado);
    }

    @Test
    void actualizarCurso_NoExiste_DeberiaRetornarNull() {
        Long idCursoInexistente = 99L;

        when(cursoRepository.existsById(idCursoInexistente)).thenReturn(false);

        Curso cursoResultado = cursoService.actualizarCurso(idCursoInexistente, new Curso());

        assertThat(cursoResultado).isNull();
        verify(cursoRepository).existsById(idCursoInexistente);
        verify(cursoRepository, never()).save(any());

        System.out.println("Resultado al no existir: " + cursoResultado);

    }

    @Test
    void actualizarCurso_ErrorRepositorio_DeberiaPropagarException() {
        Long idCurso = 2L;

        Curso cursoExistente = new Curso();
        cursoExistente.setNombreCurso("Base de Datos Aplicada I");

        when(cursoRepository.existsById(idCurso)).thenReturn(true);
        when(cursoRepository.save(cursoExistente)).thenThrow(new RuntimeException("Falla en BD"));

        assertThatThrownBy(()-> cursoService.actualizarCurso(idCurso, cursoExistente))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Falla en BD");

        verify(cursoRepository).existsById(idCurso);
        verify(cursoRepository).save(cursoExistente);

        System.out.println("Excepcion capturada correctamente");
    }

    @Test
    void obtenerCursoPorId_Existe_DeberiaRetornarCurso() {
        Long idCurso = 1L;

        Curso curso1 = new Curso();
        curso1.setIdCurso(idCurso);
        curso1.setNombreCurso("Base de Datos Aplicada II");

        when(cursoRepository.findById(idCurso)).thenReturn(Optional.of(curso1));

        Curso cursoResultado = cursoService.obtenerCursoPorId(idCurso);

        assertThat(cursoResultado).isEqualTo(curso1);
        verify(cursoRepository).findById(idCurso);

        System.out.println("Curso encontrado: " + cursoResultado.getNombreCurso());
    }

    @Test
    void obtenerCursoPorId_NoExiste_DeberiaRetornarNull() {
        Long idCursoInexistente = 99L;

        when(cursoRepository.findById(idCursoInexistente)).thenReturn(Optional.empty());

        Curso cursoResultado = cursoService.obtenerCursoPorId(idCursoInexistente);

        assertThat(cursoResultado).isNull();
        verify(cursoRepository).findById(idCursoInexistente);

        System.out.println("Resultado al no encontrar curso: " + cursoResultado);
    }

    @Test
    void eliminarPorIdCurso_DeberiaInvocarDeleteById() {
        Long idCurso = 1L;

        cursoService.eliminarPorIdCurso(idCurso);

        verify(cursoRepository).deleteById(idCurso);
        System.out.println("Curso eliminado con ID: " + idCurso);
    }

    @Test
    void estadoCursos_Activos_DeberiaRetornarListaDTO() {
        Curso curso1 = new Curso();
        CursoDTO cursoDTO = new CursoDTO();
        curso1.setIdCurso(1L);
        curso1.setNombreCurso("Bases de Innovacion");
        curso1.setEstadoCurso(true);
        cursoDTO.setIdCurso(1L);
        cursoDTO.setNombreCurso("Bases de Innovacion");
        cursoDTO.setEstadoCurso(true);

        when(cursoRepository.findByEstadoCurso(true)).thenReturn(Optional.of(List.of(curso1)));
        when(cursoToCursoDTOConverter.convert(curso1)).thenReturn(cursoDTO);

        //when
        List<CursoDTO> cursoResultado = cursoService.estadoCursos(true);

        //then
        assertThat(cursoResultado).containsExactly(cursoDTO);
        verify(cursoRepository).findByEstadoCurso(true);
        verify(cursoToCursoDTOConverter).convert(curso1);

        System.out.println("Cursos activos: " + cursoResultado);
    }

    @Test
    void estadoCursos_InactivosSinResultados_DeberiaRetornarListaVacia() {
        when(cursoRepository.findByEstadoCurso(false))
                .thenReturn(Optional.of(Collections.emptyList()));

        //when
        List<CursoDTO> cursoResultado = cursoService.estadoCursos(false);

        //then
        assertThat(cursoResultado).isEmpty();
        verify(cursoRepository).findByEstadoCurso(false);
        verifyNoInteractions(cursoToCursoDTOConverter);

        System.out.println("Cursos inactivos (vacio): " + cursoResultado);
    }

    @Test
    void cursosCreadosDesde_FechaConCursos_DeberiaRetornarListaDTO() {
        LocalDate fecha = LocalDate.of(2025, 6, 1);

        Curso curso1 = new Curso();
        CursoDTO cursoDTO = new CursoDTO();
        curso1.setIdCurso(1L);
        curso1.setNombreCurso("Inglés I");
        curso1.setFechaCreacion(fecha);
        cursoDTO.setIdCurso(1L);
        cursoDTO.setNombreCurso("Inglés I");
        cursoDTO.setFechaCreacion(fecha);

        when(cursoRepository.findByFechaCreacion(fecha)).thenReturn(List.of(curso1));
        when(cursoToCursoDTOConverter.convert(curso1)).thenReturn(cursoDTO);

        //when
        List<CursoDTO> cursoResultado = cursoService.cursosCreadosDesde(fecha);

        //Then
        assertThat(cursoResultado).containsExactly(cursoDTO);
        verify(cursoRepository).findByFechaCreacion(fecha);
        verify(cursoToCursoDTOConverter).convert(curso1);

        System.out.println("Cursos desde fecha: " + cursoResultado);
        
    }

    

}
