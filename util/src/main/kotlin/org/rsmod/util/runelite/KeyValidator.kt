package org.rsmod.util.runelite

import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_BASE_PATH
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_DUMMY_TEXT_FILE
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_KEY_PATH
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_PRIVATE_KEY
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_PUBLIC_KEY
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

/**
 * Validate your public and private key used with RuneLite Plugin hub.
 */
class KeyValidator {

    fun execute(): Boolean {
        generateSignature()
        return testOutput()
    }

    private fun generateSignature() {
        val dummyFile = Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_DUMMY_TEXT_FILE)
        if (!Files.exists(dummyFile)) {
            error("A dummy file needs to find place at: ${dummyFile.toAbsolutePath()}")
        }

        val buffer = Files.readAllBytes(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_DUMMY_TEXT_FILE))
        val s = Signature.getInstance("SHA256withRSA")
        val key = getPrivateKey()
        s.initSign(key)
        s.update(buffer)
        val output = s.sign()
        Files.write(Paths.get(PLUGIN_HUB_BASE_PATH, "$PLUGIN_HUB_DUMMY_TEXT_FILE.sha256"), output)
    }

    private fun getPrivateKey(): PrivateKey {
        val key = StringBuilder()

        if (!Files.exists(Path.of(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_KEY_PATH, PLUGIN_HUB_PRIVATE_KEY))) {
            error(
                "You need to generate the public and private key with this command:\n" +
                        "cd \"${Path.of(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_KEY_PATH).toAbsolutePath()}\" && " +
                        "openssl req -x509 -newkey rsa:1024 -keyout private.pem -out externalplugins.crt -days 365 -nodes"
            )
        }

        BufferedReader(
            FileReader(
                Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_KEY_PATH, PLUGIN_HUB_PRIVATE_KEY).toString()
            )
        ).use { reader ->
            val iterator = reader.lineSequence().iterator()
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (line.contains("-----BEGIN PRIVATE KEY-----")) {
                    continue
                }
                if (line.contains("-----END PRIVATE KEY-----")) {
                    continue
                }
                key.append(line)
            }
        }

        val encoded = Base64.getDecoder().decode(key.toString())
        val kf = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return kf.generatePrivate(keySpec)
    }

    private fun testOutput(): Boolean {
        val bytes = Files.readAllBytes(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_DUMMY_TEXT_FILE))
        val signature = Files.readAllBytes(Paths.get(PLUGIN_HUB_BASE_PATH, "$PLUGIN_HUB_DUMMY_TEXT_FILE.sha256"))
        val cert = getCertificate()
        val s = Signature.getInstance("SHA256withRSA")
        s.initVerify(cert)
        s.update(bytes)
        return s.verify(signature)
    }

    private fun getCertificate(): Certificate {
        val certFactory = CertificateFactory.getInstance("X.509")
        var cert: Certificate
        ByteArrayInputStream(
            Files.readAllBytes(
                Paths.get(
                    PLUGIN_HUB_BASE_PATH,
                    PLUGIN_HUB_KEY_PATH,
                    PLUGIN_HUB_PUBLIC_KEY
                )
            )
        ).use { `in` ->
            cert = certFactory.generateCertificate(`in`)
        }
        return cert
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val validator = KeyValidator()

            if (validator.execute()) {
                println("Everything works, your keys are ready to use!")
            } else {
                println("Your keys didn't work, you need to check your keys and try again.")
            }
        }
    }
}
