package com.example.tienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;


import com.example.tienda.model.Usuario;
import com.example.tienda.service.UsuarioService;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    //EndPoint para obtener a todos los usuarios
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();

        List<EntityModel<Usuario>> usuarioResources = usuarios.stream()
            .map(usuario -> EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(usuario.getId())).withSelfRel()
            ))
            .collect(Collectors.toList());

        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios());
        CollectionModel<EntityModel<Usuario>> resources = CollectionModel.of(usuarioResources, linkTo.withRel("usuarios"));

        return resources;
    }
    
    //Endpoint para obtener usuario por id, con validación not found    
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);

        if (usuario.isPresent()) {
            return EntityModel.of(usuario.get(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsuarioById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsuarios()).withRel("all-usuarios"));
        } else {
            throw new UsuarioNotFoundException("Student not found with id: " + id);
        }
    }


    /* 
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUsuarioById(@PathVariable String id) {
        try {
            Long userId = Long.parseLong(id);
            if (userId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "\"error\": \"El ID del usuario debe ser mayor que cero\""
                );
            }
            Optional<Usuario> usuarioOptional = usuarioService.getUsuarioById(userId);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(usuarioOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "\"error\": \"El usuario con ID " + userId + " no existe\""
                );
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "\"error\": \"El ID del usuario debe ser un número entero\""
            );
        }
    }*/

    


    //EndPoint para crear un usuario
    //Validaciones para verificar que se manden los atributos requeridos
    @PostMapping
    public ResponseEntity<Object> createUsuario(@RequestBody Usuario usuario){
    
        // Verificar si alguno de los campos requeridos está vacío o nulo
        if (usuario.getEmail() == null || usuario.getPassword() == null ||
            usuario.getName() == null || usuario.getRol() == null ||
            usuario.getDireccion1() == null || usuario.getDireccion1().isEmpty() ||
            usuario.getEmail().isEmpty() || usuario.getPassword().isEmpty() ||
            usuario.getName().isEmpty() || usuario.getRol().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "\"error\": \"Debes proporcionar todos los campos\""
            );
        }
    
        // Si todos los campos necesarios están presentes, llamar al servicio para crear el usuario
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);

        // Crear un objeto que contenga el mensaje y el usuario
        Map<String, Object> response = new HashMap<>();
        response.put("message", "El usuario se creó correctamente");
        response.put("usuario", nuevoUsuario);
        
        // Devolver el objeto creado junto con el código de estado 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    



    //EndPoint para actualizar un usuario por id
    //Validaciones para id existente, actualizar solo campos proporcionados
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario){

        // Verificar si el usuario con el ID proporcionado existe
        Optional<Usuario> usuarioExistenteOptional = usuarioService.getUsuarioById(id);
        if (!usuarioExistenteOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                "\"error\": \"El usuario con ID " + id + " no existe\""
            );
        }
    
        // Obtener el usuario existente
        Usuario usuarioExistente = usuarioExistenteOptional.get();
    
        // Actualizar solo los campos proporcionados en el objeto usuario
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            usuarioExistente.setEmail(usuario.getEmail());
        }
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(usuario.getPassword());
        }
        if (usuario.getName() != null && !usuario.getName().isEmpty()) {
            usuarioExistente.setName(usuario.getName());
        }
        if (usuario.getRol() != null && !usuario.getRol().isEmpty()) {
            usuarioExistente.setRol(usuario.getRol());
        }

        if (usuario.getDireccion1() != null && !usuario.getDireccion1().isEmpty()) {
            usuarioExistente.setDireccion1(usuario.getDireccion1());
        }

        if (usuario.getDireccion2() != null && !usuario.getDireccion2().isEmpty()) {
            usuarioExistente.setDireccion2(usuario.getDireccion2());
        }
        // Llamar al servicio para actualizar el usuario
        Usuario usuarioActualizado = usuarioService.updateUsuario(id, usuarioExistente);
        
        // Crear un objeto que contenga el mensaje y el usuario actualizado
        Map<String, Object> response = new HashMap<>();
        response.put("message", "El usuario se actualizó correctamente");
        response.put("usuario", usuarioActualizado);
        
        // Devolver el objeto creado junto con el mensaje
        return ResponseEntity.ok(response);
    }
    
    //EndPoint para eliminar un usuario por id
    //Validaciones id existente
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable Long id){


    // Verificar si el usuario con el ID proporcionado existe
    Optional<Usuario> usuarioExistenteOptional = usuarioService.getUsuarioById(id);
    if (!usuarioExistenteOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            "\"error\": \"El usuario con ID " + id + " no existe\""
        );
    }

    // Llamar al servicio para eliminar el usuario
    usuarioService.deleteUsuario(id);
    
    // Devolver una respuesta exitosa con un mensaje
    return ResponseEntity.ok().body("El usuario con ID " + id + " se eliminó correctamente");

    }


    //EndPoint para verificar inicio de sesión
    //Validaciones valores proporcionados, no null ni vacio y datos correctos
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Usuario loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
            loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "\"error\": \"Debes proporcionar un correo electrónico y una contraseña\""
            );
        }
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
    
        Optional<Usuario> usuarioOptional = usuarioService.getUsuario(email, password);
    
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(
                "\"success\": \"Sesión iniciada correctamente\""
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                "\"error\": \"Usuario o contraseña incorrectos\""
            );
        }
    }
}
