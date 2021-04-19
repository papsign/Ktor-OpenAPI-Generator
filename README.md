# Ktor OpenAPI Generator
[![](https://jitpack.io/v/1gravity/Ktor-OpenAPI-Generator.svg)](https://jitpack.io/#1gravity/Ktor-OpenAPI-Generator)
[![Build](https://github.com/1gravity/Ktor-OpenAPI-Generator/workflows/Build/badge.svg)](https://github.com/1gravity/Ktor-OpenAPI-Generator/actions)

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
- Legacy Polymorphism with use of `@DiscriminatorAnnotation()` attribute and sealed classes 

Extra Features:
- Includes Swagger-UI (enabled by default, can be managed in the `install(OpenAPIGen) { ... }` section)

## Examples

Take a look at [a few examples](https://github.com/1gravity/Ktor-OpenAPI-Generator/wiki/A-few-examples)

### Who is using it?

* <https://github.com/SerVB/e-shop>

And others... (add your name above)

## Installation

### Gradle

Step 1. Add the JitPack repository to your build file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency:
```groovy
dependencies {
        implementation 'com.1gravity:Ktor-OpenAPI-Generator:-SNAPSHOT'
}
```

### Git Submodule
Install the submodule:
```shell
git submodule add https://github.com/1gravity/Ktor-OpenAPI-Generator.git openapigen
```

Declare the folder in settings.gradle:
```groovy
...
include 'openapigen'
```
Declare the dependency in the main build.gradle
```groovy
apply plugin: 'kotlin'

...

dependencies {
    compile project(":openapigen")
    ...
}
```
