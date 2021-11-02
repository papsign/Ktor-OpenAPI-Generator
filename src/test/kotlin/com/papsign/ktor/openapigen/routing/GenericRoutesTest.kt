package com.papsign.ktor.openapigen.routing

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.HttpSecurityScheme
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.auth.*
import com.papsign.ktor.openapigen.route.path.normal.*
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import installJackson
import installOpenAPI
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.testing.*
import io.ktor.util.pipeline.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenericRoutesTest {

    @Test
    fun genericRoutesTest() {
        withTestApplication({
            installOpenAPI()
            installJackson()

            install(Authentication) {
                basic {
                    realm = "Access to the '/private' path"
                    validate { credentials ->
                        if (credentials.name == "jetbrains" && credentials.password == "foobar") {
                            UserIdPrincipal(credentials.name)
                        } else {
                            null
                        }
                    }
                }
            }


            val service = ObjectService
            apiRouting {
                route("objects") {
                    treeNodeRoute(service)
                }
                auth {
                    route("private/objects") {
                        treeNodeRoutePrivate(service)
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Get,"/objects").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }
            handleRequest(HttpMethod.Get,"/objects/1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }
            handleRequest(HttpMethod.Post,"/objects") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                setBody(""" { "name": "test" } """)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }

            fun handleRequestWithBasic(method: HttpMethod, uri: String, setup: TestApplicationRequest.() -> Unit = {}): TestApplicationCall {
                return handleRequest(method, uri) {
                    val up = Base64.getEncoder().encodeToString("jetbrains:foobar".toByteArray())
                    addHeader(HttpHeaders.Authorization, "Basic $up")
                    setup()
                }
            }

            handleRequest(HttpMethod.Get,"/private/objects").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }

            handleRequestWithBasic(HttpMethod.Get,"/private/objects") {
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }
            handleRequestWithBasic(HttpMethod.Get,"/private/objects/1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }
            handleRequestWithBasic(HttpMethod.Post,"/private/objects") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                setBody(""" { "name": "test" } """)
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue { response.contentType().match("application/json") }
            }

        }
    }


    data class ObjectNewDto(override val name: String, override val parentId: Long?) : TreeNodeNew

    data class ObjectDto(override val name: String, override val parentId: Long?, override val id: Long): TreeNodeBase

    object ObjectService : TreeNodeService<ObjectNewDto, ObjectDto> {
        override fun listNodes(parentId: Long?): List<ObjectDto> {
            if (parentId == 1L) {
                return listOf(
                    ObjectDto("Node 3", null, 3),
                    ObjectDto("Node 4", null, 4)
                )
            }
            return listOf(
                ObjectDto("Node 1", null, 1),
                ObjectDto("Node 2", null, 2)
            )
        }

        override fun createNode(dto: ObjectNewDto): ObjectDto {
            return ObjectDto(
                dto.name,
                dto.parentId,
                123
            )
        }

        override fun updateNode(dto: ObjectDto) {
            println("updateNode $dto")
        }

        override fun removeNode(nodeId: Long) {
            println("delete $nodeId")
        }

    }

    interface TreeNodeNew {
        val name: String
        val parentId: Long?
    }

    interface TreeNodeBase : TreeNodeNew {
        val id: Long
    }

    interface TreeNode : TreeNodeBase {
        val children: List<TreeNode>
    }

    interface TreeNodeService<TNodeNew : TreeNodeNew, TNode : TreeNodeBase> {
        fun listNodes(parentId: Long?): List<TNode>
        fun createNode(dto: TNodeNew): TNode
        fun updateNode(dto: TNode)
        fun removeNode(nodeId: Long)
    }

    data class PathId(@PathParam("Id") val id: Long)

    private inline fun <reified TNodeNew : TreeNodeNew, reified TNode : TreeNodeBase> NormalOpenAPIRoute.treeNodeRoute(
        service: TreeNodeService<TNodeNew, TNode>
    ) {
        route("{id}").get<PathId, List<TNode>> { params ->
            respond(service.listNodes(params.id))
        }
        route("{id}").delete<PathId, Unit> { params ->
            service.removeNode(params.id)
            pipeline.call.respond(HttpStatusCode.NoContent)
        }
        get<Unit, List<TNode>> {
            respond(service.listNodes(null))
        }
        post<Unit, TNode, TNodeNew> { _, body ->
            respond(service.createNode(body))
        }
        patch<Unit, Unit, TNode> { _, body ->
            service.updateNode(body)
            pipeline.call.respond(HttpStatusCode.NoContent)
        }
    }

    private inline fun <reified TNodeNew : TreeNodeNew, reified TNode : TreeNodeBase> OpenAPIAuthenticatedRoute<UserIdPrincipal>.treeNodeRoutePrivate(
        service: TreeNodeService<TNodeNew, TNode>
    ) {
        route("{id}").get<PathId, List<TNode>, UserIdPrincipal> { params ->
            respond(service.listNodes(params.id))
        }
        route("{id}").delete<PathId, Unit, UserIdPrincipal> { params ->
            service.removeNode(params.id)
            pipeline.call.respond(HttpStatusCode.NoContent)
        }
        get<Unit, List<TNode>, UserIdPrincipal> {
            respond(service.listNodes(null))
        }
        post<Unit, TNode, TNodeNew, UserIdPrincipal> { _, body ->
            respond(service.createNode(body))
        }
        patch<Unit, Unit, TNode, UserIdPrincipal> { _, body ->
            service.updateNode(body)
            pipeline.call.respond(HttpStatusCode.NoContent)
        }
    }

    inline fun NormalOpenAPIRoute.auth(route: OpenAPIAuthenticatedRoute<UserIdPrincipal>.() -> Unit): OpenAPIAuthenticatedRoute<UserIdPrincipal> {
        return BasicAuthProvider.apply(this).apply(route)
    }

    class UnauthorizedException(message: String) : RuntimeException(message)
    class ForbiddenException(message: String) : RuntimeException(message)

    // even if we don't need scopes at all, an empty enum has to be there, see https://github.com/papsign/Ktor-OpenAPI-Generator/issues/65
    enum class Scopes : Described

    data class ResponseError(val code: Int, val description: String, val message: String? = null) {
        constructor(statusCode: HttpStatusCode, message: String? = null) : this(statusCode.value, statusCode.description, message)
    }

    object BasicAuthProvider : AuthProvider<UserIdPrincipal> {

        // description for OpenAPI model
        override val security =
            listOf(
                listOf(
                    AuthProvider.Security(
                        SecuritySchemeModel(
                            referenceName = "basicAuth",
                            type = SecuritySchemeType.http,
                            scheme = HttpSecurityScheme.basic
                        ), emptyList<Scopes>()
                    )
                )
            )

        // gets auth information at runtime
        override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): UserIdPrincipal {
            return pipeline.context.authentication.principal()
                ?: throw UnauthorizedException("Unable to verify given credentials, or credentials are missing.")
        }

        // convert normal route to authenticated route including OpenAPI meta information
        override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<UserIdPrincipal> {
            return OpenAPIAuthenticatedRoute(route.ktorRoute.authenticate { }, route.provider.child(), this)
                .throws(
                    status = HttpStatusCode.Unauthorized.description("Your identity could not be verified."),
                    gen = { e: UnauthorizedException -> return@throws ResponseError(HttpStatusCode.Unauthorized, e.message) }
                )
                .throws(
                    status = HttpStatusCode.Forbidden.description("Your access rights are insufficient."),
                    gen = { e: ForbiddenException -> return@throws ResponseError(HttpStatusCode.Forbidden, e.message) }
                )
        }
    }
}


