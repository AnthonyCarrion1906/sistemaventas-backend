package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Usuario;
import com.empresa.sistemaventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        // Filtramos para devolver únicamente los usuarios activos
        return usuarioRepository.findAll().stream()
                .filter(usuario -> Boolean.TRUE.equals(usuario.getEstado()))
                .toList();
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        // Mantenemos la búsqueda por ID intacta para auditorías y comprobantes antiguos
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        // Garantizamos que todo usuario nuevo nazca activo por defecto
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Integer id, Usuario datos) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        existente.setUsername(datos.getUsername());
        existente.setNombreCompleto(datos.getNombreCompleto());
        
        // Se eliminó la línea de setCorreo porque la columna no existe en tu BD
        
        // Buena práctica: Solo actualizar la contraseña si el frontend envía una nueva
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        
        // Corregido: de Rol (Enum) a RolId (Integer)
        if (datos.getRolId() != null) {
            existente.setRolId(datos.getRolId());
        }
        
        // Corregido: de Activo a Estado
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : existente.getEstado());
        
        return usuarioRepository.save(existente);
    }

    // NUEVO MÉTODO: Desactivación Lógica (Soft Delete)
    public void desactivar(Integer id) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // Cambiamos el estado a false para denegar el acceso y ocultarlo de las listas
        existente.setEstado(false); 
        usuarioRepository.save(existente);
    }
}