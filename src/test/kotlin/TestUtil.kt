import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson

fun Application.installOpenAPI(): OpenAPIGen {
    return install(OpenAPIGen) {
        // basic info
        info {
            version = "0.0.1"
            title = "Test API"
            description = "The Test API"
            contact {
                name = "Support"
                email = "support@test.com"
            }
        }
        // describe the server, add as many as you want
        server("http://localhost:8080/") {
            description = "Test server"
        }
        //optional
        schemaNamer = {
            //rename DTOs from java type name to generator compatible form
            val regex = Regex("[A-Za-z0-9_.]+")
            it.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
        }
    }
}

fun Application.installJackson() {
    install(ContentNegotiation) {
        jackson()
    }
}