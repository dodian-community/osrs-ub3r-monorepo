package org.rsmod.util.runelite.plugindownloader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/*
    This tool tests your public and private key to see if it works,
    mainly to not have to test it in RuneLite itself.

    1. run: "openssl req -x509 -newkey rsa:1024 -keyout key.pem -out cert.pem -days 365 -nodes"
    2. You'll then have key.pem and cert.pem, cert.pem is what goes in RuneLite (rename it to externalplugin.crt)
    3. key.pem is used in the plugindownloader (for this project just rename it to private.pem)
 */
public class KeyValidator {

    public static void main(String[] args) throws Exception {
        generateSignature();

        if(testOutput()) {
            System.out.println("ALL OK! :)");
        } else {
            System.out.println("ITS NOT WORKING FML! :(");

        }
    }

    private static void generateSignature() throws NoSuchAlgorithmException, SignatureException, IOException, InvalidKeySpecException, InvalidKeyException {
        byte[] buffer = Files.readAllBytes(Paths.get("./test.txt"));

        Signature s = Signature.getInstance("SHA256withRSA");
        PrivateKey key = getPrivateKey();
        s.initSign(key);
        s.update(buffer);
        byte[] output = s.sign();

        Files.write(Paths.get("./test.txt.sha256"), output);
    }

    private static PrivateKey getPrivateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        StringBuilder key = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("./keys/key.pem"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("-----BEGIN PRIVATE KEY-----")) {
                    continue;
                }
                if (line.contains("-----END PRIVATE KEY-----")) {
                    continue;
                }
                key.append(line);
            }
        }

        byte[] encoded = Base64.getDecoder().decode(key.toString());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return kf.generatePrivate(keySpec);
    }

    private static boolean testOutput() throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, CertificateException {
        byte[] bytes = Files.readAllBytes(Paths.get("./test.txt"));
        byte[] signature = Files.readAllBytes(Paths.get("./test.txt.sha256"));

        Certificate cert = getCertificate();
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(cert);
        s.update(bytes);

        return s.verify(signature);
    }

    private static Certificate getCertificate() throws CertificateException, IOException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Certificate cert;
        try (InputStream in = new ByteArrayInputStream(Files.readAllBytes(Paths.get("./keys/cert.pem")))) {
            cert = certFactory.generateCertificate(in);
        }
        return cert;
    }
}
