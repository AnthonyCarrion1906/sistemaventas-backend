package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Correlativo;
import com.empresa.sistemaventas.repository.CorrelativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CorrelativoService {

    @Autowired
    private CorrelativoRepository correlativoRepository;

    /**
     * Genera y retorna el siguiente código correlativo para el tipo de documento indicado.
     * Ejemplo para tipo="PROFORMA": retorna "PRF-000001", "PRF-000002", etc.
     *
     * Usa un lock pesimista (SELECT FOR UPDATE) para garantizar que en entornos
     * concurrentes no se generen dos códigos iguales.
     *
     * @param tipo Identificador del tipo de documento (ej. "PROFORMA")
     * @return Código correlativo formateado (ej. "PRF-000001")
     * @throws RuntimeException si no existe configuración para el tipo indicado
     */
    @Transactional
    public String generarCodigo(String tipo) {
        Correlativo correlativo = correlativoRepository.findByTipoWithLock(tipo)
                .orElseThrow(() -> new RuntimeException(
                        "No existe configuración de correlativo para el tipo: " + tipo +
                        ". Agregue un registro en la tabla 'correlativos'."
                ));

        int siguienteNumero = correlativo.getUltimoNumero() + 1;
        correlativo.setUltimoNumero(siguienteNumero);
        correlativoRepository.save(correlativo);

        // Formato: PREFIJO-000001 (padding izquierdo con ceros según longitud configurada)
        String formato = "%s-%0" + correlativo.getLongitud() + "d";
        return String.format(formato, correlativo.getPrefijo(), siguienteNumero);
    }
}
