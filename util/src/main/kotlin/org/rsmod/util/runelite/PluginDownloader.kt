package org.rsmod.util.runelite

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_BASE_PATH
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_KEY_PATH
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_OUTPUT_DIRECTORY
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_PRIVATE_KEY
import io.nozemi.runescape.tools.runelite.PluginHubConstants.PLUGIN_HUB_PUBLIC_KEY
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

object PluginDownloader {

    private val mapper = ObjectMapper(YAMLFactory())
        .registerKotlinModule()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

    //private val wantedPlugins = arrayListOf(
    //    "117hd",
    //    "zulrah-helper",
    //    "skills-tab-progress-bars",
    //    "resource-packs",
    //    "the-gauntlet",
    //    "equipment-inspector"
    //)

    @JvmStatic
    fun main(args: Array<String>) {
        val keyValidator = KeyValidator()

        if (!keyValidator.execute()) {
            error("You need to have valid keys in order to proceed with the plugin downloader.")
        }

        val wantedPluginsConfigFile = Paths.get(PLUGIN_HUB_BASE_PATH, "wanted-plugins.yml")
        if (!Files.exists(wantedPluginsConfigFile)) {
            error("No config file exists for wanted plugins at: ${wantedPluginsConfigFile.toAbsolutePath()}")
        }

        val wantedPlugins: Array<String> = mapper.readValue(wantedPluginsConfigFile.toFile())

        if (!Files.exists(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_OUTPUT_DIRECTORY))) {
            Files.createDirectory(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_OUTPUT_DIRECTORY))
        }

        // clean the output folder
        Files.walk(Path.of(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_OUTPUT_DIRECTORY))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)

        val latestVersion = getLatestRuneLiteVersion()
        val latestManifest = getLatestManifest(latestVersion)

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
        val privateKey: PrivateKey =
            get(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_KEY_PATH, PLUGIN_HUB_PRIVATE_KEY).toString())
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

        Files.write(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_OUTPUT_DIRECTORY, "manifest.js"), arr)
    }


    private fun download(version: String, plugin: String, file: String) {

        println("plugin: $plugin downloading: $file")

        val response = Unirest.get("https://repo.runelite.net/plugins/$version/$plugin/$file").asBinary()

        val path = Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_OUTPUT_DIRECTORY, plugin, file)
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
        if (!Files.exists(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_PUBLIC_KEY))) {
            error(
                "You need to grab RuneLite's externalplugins.crt in order to download plugins from RuneLite's plugin hub!\n" +
                        "Put it here: ${Paths.get(PLUGIN_HUB_BASE_PATH).toAbsolutePath()}"
            )
        }

        return try {
            val certFactory = CertificateFactory.getInstance("X.509")
            certFactory.generateCertificate(
                Files.newInputStream(Paths.get(PLUGIN_HUB_BASE_PATH, PLUGIN_HUB_PUBLIC_KEY))
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
