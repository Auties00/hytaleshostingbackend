package it.auties.hosting.utils;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;

public class TokenUtils {
    public static String generateToken(String username, Set<String> groups, Long duration, String issuer) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        var privateKeyLocation = "/META-INF/resources/privatekey.pem";
        var privateKey = readPrivateKey(privateKeyLocation);

        var claimsBuilder = Jwt.claims();
        var currentTimeInSecs = currentTimeInSecs();
        return claimsBuilder.issuer(issuer)
                .subject(username)
                .issuedAt(currentTimeInSecs)
                .expiresAt(currentTimeInSecs + duration)
                .groups(groups)
                .jws()
                .signatureKeyId(privateKeyLocation)
                .sign(privateKey);
    }

    public static PrivateKey readPrivateKey(final String pemResName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream contentIS = TokenUtils.class.getResourceAsStream(pemResName)) {
            byte[] tmp = new byte[4096];
            int length = contentIS.read(tmp);
            return decodePrivateKey(new String(tmp, 0, length, StandardCharsets.UTF_8));
        }
    }

    public static PrivateKey decodePrivateKey(final String pemEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encodedBytes = toEncodedBytes(pemEncoded);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    public static byte[] toEncodedBytes(final String pemEncoded) {
        final String normalizedPem = removeBeginEnd(pemEncoded);
        return Base64.getDecoder().decode(normalizedPem);
    }

    public static String removeBeginEnd(String pem) {
        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

    public static int currentTimeInSecs() {
        long currentTimeMS = System.currentTimeMillis();
        return (int) (currentTimeMS / 1000);
    }
}