import java.net.URI
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.parchmentmc.org/")
        maven("https://repo.spongepowered.org/maven/")
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()

        maven("https://maven.minecraftforge.net/")
        maven("https://maven.parchmentmc.org/")
        maven("https://repo.spongepowered.org/maven/")

        maven("https://www.cursemaven.com")
        maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        maven("https://maven.latvian.dev/releases")
        maven("https://maven.architectury.dev")
        maven("https://code.redspace.io/releases")
        maven("https://maven.theillusivec4.top")
        maven("https://maven.blamejared.com")
        maven("https://maven.kosmx.dev/")
    }
}



plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"

}
rootProject.name ="irons_spells_js"