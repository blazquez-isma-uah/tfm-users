package com.tfm.bandas.users.utils;

import com.tfm.bandas.users.exception.PreconditionFailedException;
import com.tfm.bandas.users.exception.PreconditionRequiredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public final class EtagUtils {
  private EtagUtils() {}

  // Formato débil: W/"<version>"
  public static String toEtag(int version) {
    return "W/\"" + version + "\"";
  }

  // Acepta W/"n" o "n"
  public static int parseIfMatchToVersion(String ifMatch) {
    if (ifMatch == null || ifMatch.isBlank())
      throw new PreconditionRequiredException("La cabecera If-Match es obligatoria.");
    String v = ifMatch.trim();
    if ("*".equals(v)) {
      throw new IllegalArgumentException("El valor If-Match '*' no está permitido para esta operación.");
    }
    if (v.startsWith("W/")) v = v.substring(2).trim();
    if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length()-1);
    try {
      return Integer.parseInt(v);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("El valor de If-Match '" + ifMatch + "' no es válido.");
    }
  }

  public static <T> ResponseEntity<T> withEtag(ResponseEntity.BodyBuilder builder, int version, T body) {
    return builder.header(HttpHeaders.ETAG, toEtag(version)).body(body);
  }

  public static void compareVersion(int ifMatchVersion, int entityVersion) {
    // If-Match contra @Version
    if (entityVersion != ifMatchVersion) {
      throw new PreconditionFailedException("La versión del recurso ha cambiado. Se esperaba " + ifMatchVersion + " pero se encontró " + entityVersion + ".");
    }
  }
}
