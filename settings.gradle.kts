pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }

    }
}

rootProject.name = "trace"

include(":app")

include(":core")
include(":core:data")
include(":core:designsystem")
include(":core:common")
include(":core:network")
include(":core:navigation")
include(":core:domain")
include(":core:analytics")
include(":core:datastore")
include(":core:common-ui")

include(":feature")
include(":feature:splash")
include(":feature:main")
include(":feature:auth")
include(":feature:home")
include(":feature:mission")
include(":feature:mypage")



