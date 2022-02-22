package org.rsmod.game.config

import net.dodian.common.models.WorldLocation
import net.dodian.common.models.WorldProperty
import org.rsmod.game.GameEnv
import org.rsmod.game.model.map.Coordinates
import java.math.BigInteger
import java.nio.file.Path

data class GameConfig(
    val name: String,
    val majorRevision: Int,
    val minorRevision: Int,
    val port: Int,
    val host: String,
    val dataPath: Path,
    val pluginPath: Path,
    val home: Coordinates,
    val env: GameEnv,
    val centralServer: String
) {

    val cachePath: Path
        get() = dataPath.resolve("cache")

    val rsaPath: Path
        get() = dataPath.resolve(Path.of("rsa", "key.pem"))

    val internalConfig: Path
        get() = dataPath.resolve("internal.yml")

    val pluginConfigPath: Path
        get() = pluginPath.resolve("resources")
}

data class WorldConfig(
    val id: Int,
    val activity: String,
    val flags: List<WorldProperty>,
    val location: WorldLocation
)

data class RsaConfig(
    val exponent: BigInteger,
    val modulus: BigInteger
) {

    val isEnabled: Boolean
        get() = this !== DISABLED_RSA

    companion object {
        val DISABLED_RSA = RsaConfig(BigInteger.ZERO, BigInteger.ZERO)
    }
}

data class InternalConfig(
    val gameTickDelay: Int,
    val loginsPerCycle: Int,
    val logoutsPerCycle: Int,
    val actionsPerCycle: Int
)
