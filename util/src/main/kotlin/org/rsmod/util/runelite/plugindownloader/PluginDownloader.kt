package org.rsmod.util.runelite.plugindownloader

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

/*
    This tool downloads the wanted plugins from RuneLite's plugin hub.
    1. If you haven't already, use the KeyValidator to validate your public and private key
    2. Place the private.pem in the root of this project or module (depending on whether you run this with Gradle or not)
    3. You need to grab the externalplugins.crt from RuneLite in order to download the plugins
    4. Execute, and your output folder will contain what goes on your HTTP server
    5. Then all you have to do is put your own externalplugins.crt (which you've verified in the KeyValidator) in your RuneLite
 */
object PluginDownloader {

    @JvmStatic
    fun main(args: Array<String>) {
        if (!Files.exists(Paths.get("./output"))) {
            Files.createDirectory(Paths.get("./output/"))
        }

        // clean the output folder
        Files.walk(Path.of("./output/"))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);

        val latestVersion = getLatestRuneLiteVersion()
        val latestManifest = getLatestManifest(latestVersion)

        val wantedPlugins = arrayListOf(
            "117hd",
            "zulrah-helper",
            "skills-tab-progress-bars",
            "resource-packs",
            "the-gauntlet",
            "equipment-inspector"
        )

        val availablePlugins: JSONArray = latestManifest.array
        val filteredList =
            availablePlugins.filterIsInstance<JSONObject>()
                .filter { p -> wantedPlugins.contains(p.get("internalName")) }

        filteredList.forEach { p ->
            val internalName = p.getString("internalName")
            val commit = p.getString("commit")

            download(latestVersion, internalName, "$commit-sources.zip")
            download(latestVersion, internalName, "$commit.jar")
            download(latestVersion, internalName, "$commit.log")
            download(latestVersion, internalName, "$commit.png")
        }

        val output = filteredList.toString().encodeToByteArray()

        val signature1 = Signature.getInstance("SHA256withRSA")
        val privateKey: PrivateKey = get("private.pem")
        signature1.initSign(privateKey)
        signature1.update(output)
        val signature = signature1.sign()

        val buffer = ByteBuffer.allocate(signature.size + 4 + output.size)

        buffer.putInt(signature.size)
        buffer.put(signature)
        buffer.put(output)

        buffer.rewind()
        val arr = ByteArray(buffer.remaining())

        buffer[arr]

        Files.write(Paths.get("./output/manifest.js"), arr)
    }

    private fun download(version: String, plugin: String, file: String) {

        println("plugin: $plugin downloading: $file")

        val response = Unirest.get("https://repo.runelite.net/plugins/$version/$plugin/$file").asBinary()

        val path = Paths.get("./output/$plugin/$file")
        Files.createDirectories(path.parent)

        Files.write(path, response.body.readAllBytes())
    }

    private fun getLatestManifest(version: String): JsonNode {
        val response: HttpResponse<InputStream> =
            Unirest.get("https://repo.runelite.net/plugins/$version/manifest.js").asBinary()
        val stream = DataInputStream(response.body)

        val signatureSize = stream.readInt()
        val signatureBuffer = ByteArray(signatureSize)
        stream.read(signatureBuffer, 0, signatureSize)

        val remaining = stream.available()
        val buffer = ByteArray(remaining)
        stream.read(buffer, 0, remaining)

        val s = Signature.getInstance("SHA256withRSA")
        s.initVerify(loadRuneLiteCertificate())
        s.update(buffer)

        if (!s.verify(signatureBuffer)) {
            throw RuntimeException("Unable to verify external plugin manifest")
        }

        return JsonNode(String(buffer))
    }

    private fun getLatestRuneLiteVersion(): String {
        val response: HttpResponse<JsonNode> =
            Unirest.get("https://api.github.com/repos/runelite/runelite/tags").asJson()
        return response.body.array.getJSONObject(0).getString("name").replace("runelite-parent-", "")
    }

    private fun loadRuneLiteCertificate(): Certificate? {
        return try {
            val certFactory = CertificateFactory.getInstance("X.509")
            certFactory.generateCertificate(
                Files.newInputStream(
                    Paths.get("externalplugins.crt")
                )
            )
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        }
    }

    fun get(filename: String): PrivateKey {
        val key = StringBuilder()
        BufferedReader(FileReader(filename)).use { reader ->
            reader.forEachLine { line ->
                if (!line.contains("-----BEGIN PRIVATE KEY-----") && !line.contains("-----END PRIVATE KEY-----")) {
                    key.append(line)
                }
            }
        }
        val encoded = Base64.getDecoder().decode(key.toString())
        val kf = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return kf.generatePrivate(keySpec)

    }
}
