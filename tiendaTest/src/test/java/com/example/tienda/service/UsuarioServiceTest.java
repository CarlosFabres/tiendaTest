package com.example.tienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.tienda.model.Usuario;
import com.example.tienda.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @InjectMocks
    private UsuarioServiceImpl usuarioServicio;

    @Mock
    private UsuarioRepository usuarioeRepositoryMock;

    @Test
    public void guardarUsuarioTest() {

        Usuario usuario = new Usuario();
        usuario.setName("Jose Rondon");

        when(usuarioeRepositoryMock.save(any())).thenReturn(usuario);

        Usuario resultado = usuarioServicio.createUsuario(usuario);

        assertEquals("Jose Rondon", resultado.getName());
    }
}
