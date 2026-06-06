package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.OrdenCompra;
import com.empresa.sistemaventas.service.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordenes-compra")
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    // Directorio donde se guardarán los archivos físicamente
    private final String UPLOAD_DIR = "uploads/comprobantes/";

    @GetMapping
    public ResponseEntity<List<OrdenCompra>> listarOrdenes() {
        return new ResponseEntity<>(ordenCompraService.obtenerTodas(), HttpStatus.OK);
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<OrdenCompra>> listarPorProveedor(@PathVariable Integer proveedorId) {
        return new ResponseEntity<>(ordenCompraService.obtenerPorProveedor(proveedorId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenCompra ordenCompra) {
        try {
            return new ResponseEntity<>(ordenCompraService.guardar(ordenCompra), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/recibir")
    public ResponseEntity<?> recibirOrden(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(ordenCompraService.recibir(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{id}/comprobante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> adjuntarComprobante(@PathVariable Integer id,
                                                 @RequestParam("archivo") MultipartFile archivo,
                                                 @RequestParam(required = false) String serie,
                                                 @RequestParam(required = false) String correlativo) {
        try {
            if (archivo.getSize() > 5 * 1024 * 1024) {
                return new ResponseEntity<>("El comprobante no puede superar 5MB", HttpStatus.BAD_REQUEST);
            }

            // 1. Crear el directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generar un nombre de archivo único para evitar que se sobreescriban
            String fileName = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 3. Guardar el archivo físicamente en la carpeta
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Concatenar serie y correlativo para la base de datos
            String nroComprobante = (serie != null ? serie : "") + "-" + (correlativo != null ? correlativo : "");
            if (nroComprobante.equals("-")) {
                nroComprobante = "S/N"; // Sin número si no envían nada
            }

            // 5. Generar la "URL" o ruta relativa que guardaremos en MariaDB
            String comprobanteUrl = "/archivos/comprobantes/" + fileName;

            // 6. Llamar al servicio actualizado
            OrdenCompra orden = ordenCompraService.adjuntarComprobante(id, comprobanteUrl, nroComprobante);
            
            return new ResponseEntity<>(orden, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("No se pudo guardar el archivo físicamente", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}