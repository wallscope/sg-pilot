package com.sg.app.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sg.app.rdf.URI
import com.sg.app.rdf.URIDeserializer
import com.sg.app.rdf.URISerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.web.server.WebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.ServletContext
import javax.servlet.ServletException


/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
class WebConfigurer(
    private val env: Environment,
) : ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>, WebMvcConfigurer{

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        if (env.activeProfiles.isNotEmpty()) {
            log.info("Web application configuration, using profiles: {}", *env.activeProfiles as Array<*>)
        }

        log.info("Web application fully configured")
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    override fun customize(server: WebServerFactory) {
        // When running in an IDE or with ./gradlew bootRun, set location of the static web assets.
        setLocationForStaticAssets(server)
    }

    private fun setLocationForStaticAssets(server: WebServerFactory) {
        if (server is ConfigurableServletWebServerFactory) {
            val prefixPath = resolvePathPrefix()
            val root = File(prefixPath + "build/resources/main/static/")
            log.debug("SETTING STATIC ASSET ROOT TO: $root")
            if (root.exists() && root.isDirectory) {
                server.setDocumentRoot(root)
            }
        }
    }

    /**
     * Resolve path prefix to static resources.
     */
    private fun resolvePathPrefix(): String {
        val fullExecutablePath = decode(this.javaClass.getResource("").path, StandardCharsets.UTF_8)
        val rootPath = Paths.get(".").toUri().normalize().path
        val extractedPath = fullExecutablePath.replace(rootPath, "")
        val extractionEndIndex = extractedPath.indexOf("build/")
        if (extractionEndIndex <= 0) {
            return ""
        }
        return extractedPath.substring(0, extractionEndIndex)
    }

    /**
     * Ensure client-side paths redirect to index.html because client handles routing. NOTE: Do NOT use @EnableWebMvc or it will break this.
     */
    override fun addViewControllers(registry: ViewControllerRegistry) {
        // Map "/"
        registry.addViewController("/")
            .setViewName("forward:/index.html")

        // Map "/word", "/word/word", and "/word/word/word" - except for anything starting with "/api/..." or ending with
        // a file extension like ".js" - to index.html. By doing this, the client receives and routes the url. It also
        // allows client-side URLs to be bookmarked.

        // Single directory level - no need to exclude "api"
        registry.addViewController("/{x:[\\w\\-]+}")
            .setViewName("forward:/index.html")
        // Multi-level directory path, need to exclude "api" on the first part of the path
        registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}")
            .setViewName("forward:/index.html")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**").addResourceLocations("/")
    }

    // By overriding the default ObjectMapper configuration we make sure to use the Kotlin Object mapper
    // We also register the URI Custom (De)Serialization
    @Bean
    fun viewsObjectMapper(): ObjectMapper {
        return mapper
    }

    @Bean
    fun jackson2Converter(): MappingJackson2HttpMessageConverter? {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = mapper
        return converter
    }

    companion object {
        val mapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
            val mod = SimpleModule()
                .addDeserializer(URI::class.java, URIDeserializer())
                .addSerializer(URI::class.java, URISerializer())
                .addSerializer(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime>() {
                    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
                        gen.writeString(value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    }
                })
            registerModule(mod)
        }
    }
}
