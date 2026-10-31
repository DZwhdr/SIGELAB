package com.sigelab.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sigelab.repository.ConexionBD;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class FacialAuthService {

    private static final String API_URL = "http://127.0.0.1:8000/verify";

    private Connection obtenerConexionBD() {
        return ConexionBD.obtenerConexion();
    }

    /**
     * Registra las 52 muestras biométricas en la tabla usuario_biometria de SQL Server.
     */
    public boolean registrarMuestrasBiometricas(List<File> listaFotos, String username) {
        if (listaFotos == null || listaFotos.isEmpty()) {
            System.err.println("Error: La lista de fotografías recibida está vacía.");
            return false;
        }

        if (username == null || username.trim().isEmpty()) {
            System.err.println("Error: El nombre de usuario especificado no es válido.");
            return false;
        }

        String usernameLimpio = username.trim();
        // Se busca primero usando 'id' que es la clave primaria real en tu tabla usuario
        String sqlUsuario = "SELECT id FROM usuario WHERE LOWER(username) = LOWER(?)";
        String sqlInsertBiometria = "INSERT INTO usuario_biometria (id_usuario, foto) VALUES (?, ?)";

        try (Connection conn = obtenerConexionBD()) {
            if (conn == null) {
                System.err.println("Error: No se pudo establecer conexión con la base de datos.");
                return false;
            }

            int idUsuario = -1;

            // 1. Intentar buscar por 'id'
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUsuario)) {
                stmtUser.setString(1, usernameLimpio);
                try (ResultSet rs = stmtUser.executeQuery()) {
                    if (rs.next()) {
                        idUsuario = rs.getInt(1);
                    }
                }
            } catch (Exception ex) {
                // Respaldo por si la columna se llamara 'id_usuario'
                String sqlFallback = "SELECT id_usuario FROM usuario WHERE LOWER(username) = LOWER(?)";
                try (PreparedStatement stmtUserFallback = conn.prepareStatement(sqlFallback)) {
                    stmtUserFallback.setString(1, usernameLimpio);
                    try (ResultSet rsFallback = stmtUserFallback.executeQuery()) {
                        if (rsFallback.next()) {
                            idUsuario = rsFallback.getInt(1);
                        }
                    }
                }
            }

            if (idUsuario == -1) {
                System.err.println("Error: No se encontró al usuario '" + usernameLimpio + "' en la BDD.");
                return false;
            }

            // Inserción masiva del lote de 52 muestras como BLOB
            conn.setAutoCommit(false);
            try (PreparedStatement stmtBio = conn.prepareStatement(sqlInsertBiometria)) {
                for (File fotoFile : listaFotos) {
                    byte[] bytesFoto = Files.readAllBytes(fotoFile.toPath());
                    stmtBio.setInt(1, idUsuario);
                    stmtBio.setBytes(2, bytesFoto);
                    stmtBio.addBatch();
                }
                stmtBio.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

            System.out.println("Se guardaron " + listaFotos.size() + " muestras biométricas en SQL Server para el usuario: " + usernameLimpio);
            return true;

        } catch (Exception e) {
            System.err.println("Error al registrar las muestras biométricas en SQL Server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera de SQL Server la muestra biométrica más reciente del usuario (ORDER BY DESC)
     * y genera un archivo temporal para la comparación.
     */
    public File obtenerFotoReferenciaBaseDatos(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String usernameLimpio = username.trim();

        // Consulta configurada con 'u.id' y ORDER BY para obtener la última muestra guardada
        String sql = "SELECT TOP 1 b.foto FROM usuario_biometria b " +
                "JOIN usuario u ON b.id_usuario = u.id " +
                "WHERE LOWER(u.username) = LOWER(?) " +
                "ORDER BY b.id_biometria DESC";

        try (Connection conn = obtenerConexionBD()) {
            if (conn == null) return null;

            byte[] bytesFoto = null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usernameLimpio);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        bytesFoto = rs.getBytes("foto");
                    }
                }
            } catch (Exception ex) {
                // Respaldo en caso de que en el JOIN la columna en usuario sea 'id_usuario'
                String sqlFallback = "SELECT TOP 1 b.foto FROM usuario_biometria b " +
                        "JOIN usuario u ON b.id_usuario = u.id_usuario " +
                        "WHERE LOWER(u.username) = LOWER(?) " +
                        "ORDER BY b.id_biometria DESC";
                try (PreparedStatement stmtFallback = conn.prepareStatement(sqlFallback)) {
                    stmtFallback.setString(1, usernameLimpio);
                    try (ResultSet rsFallback = stmtFallback.executeQuery()) {
                        if (rsFallback.next()) {
                            bytesFoto = rsFallback.getBytes("foto");
                        }
                    }
                }
            }

            if (bytesFoto != null && bytesFoto.length > 0) {
                File tempFile = File.createTempFile("ref_bd_" + usernameLimpio + "_", ".jpg");
                tempFile.deleteOnExit(); // Se elimina automáticamente al cerrar la app
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(bytesFoto);
                }
                return tempFile;
            } else {
                System.err.println("Advertencia: No se encontraron fotos para el usuario: " + usernameLimpio);
            }

        } catch (Exception e) {
            System.err.println("Error al recuperar foto biométrica desde SQL Server: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compara la foto en vivo capturada contra la imagen almacenada obtenida de SQL Server.
     */
    public boolean autenticarRostro(File fotoCamara, File fotoBaseDatos) {
        if (fotoCamara == null || !fotoCamara.exists() || fotoBaseDatos == null || !fotoBaseDatos.exists()) {
            System.err.println("Error: Uno o ambos archivos de imagen no existen para la autenticación.");
            return false;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadRequest = new HttpPost(API_URL);

            ContentType contentTypeCam = fotoCamara.getName().toLowerCase().endsWith(".png") ? ContentType.IMAGE_PNG : ContentType.IMAGE_JPEG;
            ContentType contentTypeDb = fotoBaseDatos.getName().toLowerCase().endsWith(".png") ? ContentType.IMAGE_PNG : ContentType.IMAGE_JPEG;

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image_cam", fotoCamara, contentTypeCam, fotoCamara.getName());
            builder.addBinaryBody("image_db", fotoBaseDatos, contentTypeDb, fotoBaseDatos.getName());

            uploadRequest.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(uploadRequest)) {
                String responseString = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();

                if (jsonResponse.has("match")) {
                    return jsonResponse.get("match").getAsBoolean();
                } else if (jsonResponse.has("success")) {
                    return jsonResponse.get("success").getAsBoolean();
                } else if (jsonResponse.has("verified")) {
                    return jsonResponse.get("verified").getAsBoolean();
                }

                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio de verificación facial: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}