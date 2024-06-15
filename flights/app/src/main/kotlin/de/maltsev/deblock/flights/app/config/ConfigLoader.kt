package de.maltsev.deblock.flights.app.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.yamlMap
import de.maltsev.deblock.yaml.duration
import de.maltsev.deblock.yaml.findString
import de.maltsev.deblock.yaml.int
import de.maltsev.deblock.yaml.list
import de.maltsev.deblock.yaml.obj
import de.maltsev.deblock.yaml.string
import io.ktor.server.auth.UserPasswordCredential
import java.io.File

object ConfigLoader {

    fun loadConfig(file: File): FlightsAppConfig = file.toYamlMap().run {
        FlightsAppConfig(
            server = obj("server").toServer(),
            integration = obj("integration").toIntegration(),
        )
    }

    private fun File.toYamlMap() = Yaml.default.parseToYamlNode(readText()).yamlMap

    private fun YamlMap.toServer() = ServerConfig(
        port = int("port"),
        auth = obj("auth").toAuth(),
    )

    private fun YamlMap.toAuth() = ServerAuthConfig(
        basic = obj("basic").toBasicAuth(),
    )

    private fun YamlMap.toBasicAuth() = BasicAuthConfig(
        users = list("users").toUsers(),
    )

    private fun YamlList.toUsers() = items.map {
        it.yamlMap.toUser()
    }

    private fun YamlMap.toUser() = UserPasswordCredential(
        name = string("username"),
        password = string("password"),
    )

    private fun YamlMap.toIntegration() = IntegrationConfig(
        toughJet = obj("toughJet").toClientConfig(),
        crazyAir = obj("crazyAir").toClientConfig(),
    )

    private fun YamlMap.toClientConfig() = ClientConfig(
        url = string("url"),
        apiKey = findString("apiKey"),
        connectionTimeout = duration("connectionTimeout"),
        requestTimeout = duration("requestTimeout"),
    )
}
