package com.example.tienda.service;

import com.example.tienda.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface UsuarioService {

    List<Usuario> getAllUsuarios();

    Optional<Usuario> getUsuarioById(Long id);

    Optional<Usuario> getUsuario(String email, String password)
;
    Usuario createUsuario(Usuario usuario);

    Usuario updateUsuario(Long id, Usuario usuario);

    void deleteUsuario(Long id);

}                                                                         