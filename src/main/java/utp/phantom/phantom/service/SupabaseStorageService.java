package utp.phantom.phantom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utp.phantom.phantom.exception.StorageException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

/**
 * Sube y elimina imágenes de productos en Supabase Storage.
 * La service_role key vive solo aquí (variable de entorno); el navegador nunca la ve.
 */
@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB
    private static final String PREFIJO_PRODUCTO = "prod";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String supabaseUrl;
    private final String serviceKey;
    private final String bucketProductos;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String serviceKey,
            @Value("${supabase.bucket.productos}") String bucketProductos) {
        this.supabaseUrl = supabaseUrl.endsWith("/")
                ? supabaseUrl.substring(0, supabaseUrl.length() - 1)
                : supabaseUrl;
        this.serviceKey = serviceKey;
        this.bucketProductos = bucketProductos;
    }

    /**
     * Valida la imagen, la renombra como prod_{uuid}.{ext}, la sube al bucket
     * y devuelve su URL pública.
     *
     * @throws IllegalArgumentException si el archivo no es válido (mensaje apto para el usuario)
     * @throws StorageException         si Supabase no responde o rechaza la subida
     */
    public String subirImagenProducto(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Selecciona una imagen.");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("La imagen supera el tamaño máximo permitido (5 MB).");
        }

        byte[] bytes;
        try {
            bytes = archivo.getBytes();
        } catch (IOException e) {
            throw new StorageException("No se pudo leer el archivo subido.", e);
        }

        // El tipo se detecta por el contenido real, no por el nombre ni el Content-Type del cliente.
        TipoImagen tipo = detectarTipo(bytes);
        if (tipo == null) {
            throw new IllegalArgumentException("Formato no permitido. Usa JPG, PNG o WEBP.");
        }

        String nombreArchivo = PREFIJO_PRODUCTO + "_" + UUID.randomUUID() + "." + tipo.extension;

        HttpRequest request = HttpRequest.newBuilder(URI.create(urlObjeto(nombreArchivo)))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + serviceKey)
                .header("apikey", serviceKey)
                .header("Content-Type", tipo.mime)
                .header("x-upsert", "false")
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();

        enviar(request, "subir", nombreArchivo);
        return urlPublica(nombreArchivo);
    }

    /**
     * Elimina del bucket la imagen a la que apunta la URL.
     * Solo actúa sobre URLs de NUESTRO bucket (ignora rutas estáticas como /Imagenes/x.jpg
     * o enlaces externos) y nunca lanza excepción: un fallo aquí no debe romper el flujo.
     */
    public void eliminarPorUrl(String url) {
        if (!esImagenDelBucket(url)) {
            return;
        }
        String nombreArchivo = url.substring(prefijoPublico().length());
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(urlObjeto(nombreArchivo)))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .DELETE()
                    .build();
            enviar(request, "eliminar", nombreArchivo);
        } catch (RuntimeException e) {
            log.warn("No se pudo eliminar la imagen '{}' del bucket: {}", nombreArchivo, e.getMessage());
        }
    }

    public boolean esImagenDelBucket(String url) {
        return url != null && url.startsWith(prefijoPublico()) && url.length() > prefijoPublico().length();
    }

    // ---------------------------------------------------------------- internos

    private void enviar(HttpRequest request, String operacion, String nombreArchivo) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                log.error("Supabase Storage rechazó '{}' de '{}': HTTP {} - {}",
                        operacion, nombreArchivo, status, response.body());
                throw new StorageException("No se pudo " + operacion + " la imagen. Inténtalo de nuevo.");
            }
        } catch (IOException e) {
            throw new StorageException("No se pudo conectar con el almacenamiento de imágenes.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StorageException("La operación con el almacenamiento fue interrumpida.", e);
        }
    }

    private String urlObjeto(String nombreArchivo) {
        return supabaseUrl + "/storage/v1/object/" + codificar(bucketProductos) + "/" + codificar(nombreArchivo);
    }

    private String prefijoPublico() {
        return supabaseUrl + "/storage/v1/object/public/" + codificar(bucketProductos) + "/";
    }

    private String urlPublica(String nombreArchivo) {
        return prefijoPublico() + codificar(nombreArchivo);
    }

    private static String codificar(String segmento) {
        return URLEncoder.encode(segmento, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /** Detección por "magic bytes": JPEG, PNG y WEBP. */
    private static TipoImagen detectarTipo(byte[] b) {
        if (b.length >= 3
                && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return TipoImagen.JPG;
        }
        if (b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return TipoImagen.PNG;
        }
        if (b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return TipoImagen.WEBP;
        }
        return null;
    }

    private enum TipoImagen {
        JPG("jpg", "image/jpeg"),
        PNG("png", "image/png"),
        WEBP("webp", "image/webp");

        final String extension;
        final String mime;

        TipoImagen(String extension, String mime) {
            this.extension = extension;
            this.mime = mime;
        }
    }
}
