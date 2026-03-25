pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EnterpriseMobileApp"

include(":app")

// Core modules
include(":core:domain")
include(":core:network")
include(":core:uicomponents")
include(":core:observability")

// Feature modules
include(":feature:auth")
include(":feature:explore")
include(":feature:shop")
include(":feature:aiconnect")
include(":feature:rewards")
