package xyz.lavoute.web.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * This class is used for various cryptographic operations.
 * From hashing to encryption, it should kept as stateless as possible.
 */
@Service
public class CryptoService {
    @Value("${app.signed-url.secret")
    private String secret;

    public String generate_HMAC_Signature(String algorithm, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(keySpec);

        return HexFormat
                .of()
                .formatHex(mac.doFinal(data.getBytes()));
    }

    /**
     * Generates a HMAC SHA-256 signature
     *
     * @see <a href="https://medium.com/@raditya.mit/beyond-checksums-securing-file-transfers-with-hmac-sha256-91ffb3cff3bd">HMAC_SHA-256</a>
     *
     * @param data
     * @return HMAC SHA-256 signature
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public String generate_HMAC_SHA256_Signature(String data) throws InvalidKeyException, NoSuchAlgorithmException {
        String algorithm = "HmacSHA256";
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(keySpec);

        return HexFormat
                .of()
                .formatHex(mac.doFinal(data.getBytes()));
    }
}
