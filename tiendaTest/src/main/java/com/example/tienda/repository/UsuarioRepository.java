package com.example.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.tienda.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmailAndPassword(String email, String password);
}
