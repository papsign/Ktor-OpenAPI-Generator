# Ktor OpenAPI Generator
[![](https://jitpack.io/v/papsign/Ktor-OpenAPI-Generator.svg)](https://jitpack.io/#papsign/Ktor-OpenAPI-Generator)
[![Build](https://github.com/papsign/Ktor-OpenAPI-Generator/workflows/Build/badge.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator/actions)

The Ktor OpenAPI Generator is a library to automatically generate the descriptor as you route your ktor application.

Ktor OpenAPI Generator is:
- Modular
- Strongly typed
- Explicit

Currently Supported:
- Authentication interoperability with strongly typed Principal (OAuth only, see TestServer in tests)
- Content Negotiation interoperability (see TestServer in tests)
- Custom response codes (as parameter in `@Response`)
- Automatic and custom content Type routing and parsing (see `com.papsign.ktor.openapigen.content.type`, Binary Parser and default JSON parser (that uses the ktor implicit parsing/serializing))
- Exception handling (use `.throws(ex) {}` in the routes with an APIException object) with Status pages interop (with .withAPI in the StatusPages configuration)
- tags (`.tag(tag) {}` in route with a tag object, currently must be an enum, but may be subject to change)
- Spec compliant Parameter Parsing (see basic example)

Extra Features:
- Includes Swagger-UI (enabled by default, can be managed in the `install(OpenAPIGen) { ... }` section)

It is inspired by ktor Locations, but makes no use of it.

## Examples

Take a look at [a few examples](https://github.com/papsign/Ktor-OpenAPI-Generator/wiki/A-few-examples)

## Installation

### Gradle

Step 1. Add the JitPack repository to your build file:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency:
```
dependencies {
        implementation 'com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT'
}
```

### Git Submodule
Install the submodule:
```
git submodule add https://github.com/papsign/Ktor-OpenAPI-Generator.git openapigen
```

Declare the folder in settings.gradle:
```
...
include 'openapigen'
```
Declare the dependency in the main build.gradle
```
apply plugin: 'kotlin'

...

dependencies {
    compile project(":openapigen")
    ...
}
```

## Expose the OpenAPI.json and swager-ui

```
application.routing {
    get("/openapi.json") {
        call.respond(application.openAPIGen.api.serialize())
    }
    get("/") {
        call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
    }
}
```
