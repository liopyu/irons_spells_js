import org.gradle.api.tasks.compile.JavaCompile
import java.text.SimpleDateFormat as JSdf
import java.util.Date as JDate
plugins {
    eclipse
    idea
    `maven-publish`
    id("net.minecraftforge.gradle") version "6.0.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

version = "${property("minecraft_version")}-${property("mod_version")}-release"
group = property("mod_group_id").toString()

base {
    archivesName.set(property("mod_id").toString())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

println(
    "Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}"
)

minecraft {
    mappings(property("mapping_channel").toString(), property("mapping_version").toString())

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    copyIdeResources.set(true)

    runs {
        create("client") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", property("mod_id").toString())
            property("production", "true")

            if (project.hasProperty("mc_uuid")) {
                args("--uuid", project.property("mc_uuid").toString())
            }
            if (project.hasProperty("mc_username")) {
                args("--username", project.property("mc_username").toString())
            }
            if (project.hasProperty("mc_accessToken")) {
                args("--accessToken", project.property("mc_accessToken").toString())
            }

            mods {
                create(property("mod_id").toString()) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", property("mod_id").toString())
            property("production", "true")

            mods {
                create(property("mod_id").toString()) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("gameTestServer") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", property("mod_id").toString())

            mods {
                create(property("mod_id").toString()) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")

            args(
                "--mod", property("mod_id").toString(),
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )

            mods {
                create(property("mod_id").toString()) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.latvian.dev/releases")
        content { includeGroup("dev.latvian.mods") }
    }
    maven { url = uri("https://maven.architectury.dev") }
    maven {
        url = uri("https://cursemaven.com")
        content { includeGroup("curse.maven") }
    }
    maven {
        name = "Iron's Maven"
        url = uri("https://code.redspace.io/releases")
    }

    maven { url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") }
    maven { url = uri("https://maven.theillusivec4.top") }
    maven { url = uri("https://maven.blamejared.com") }
    maven { url = uri("https://maven.kosmx.dev/") }
}

mixin {
    add(sourceSets.main.get(), "${property("mod_id")}.refmap.json")
    config("${property("mod_id")}.mixins.json")
}
dependencies {
    "minecraft"("net.minecraftforge:forge:${property("minecraft_version")}-${property("forge_version")}")

    annotationProcessor("org.spongepowered:mixin:${property("mixin_version")}:processor")

    implementation(fg.deobf("dev.latvian.mods:kubejs-forge:${property("kubejs_version")}"))
    implementation(fg.deobf("dev.latvian.mods:rhino-forge:${property("rhino_version")}"))
    implementation(fg.deobf("dev.architectury:architectury-forge:${property("architectury_version")}"))
    implementation(fg.deobf("io.redspace.ironsspellbooks:irons_spellbooks:${property("minecraft_version")}-${property("irons_spells_version")}"))
    implementation(fg.deobf("top.theillusivec4.curios:curios-forge:${property("curios_version")}"))

    compileOnly(fg.deobf("curse.maven:entityjs-967617:${property("entityjs_file_id")}"))
    compileOnly(fg.deobf("software.bernie.geckolib:geckolib-forge-${property("minecraft_version")}:${property("geckolib_version")}"))
    compileOnly(fg.deobf("curse.maven:probejs-585406:5227399"))

    runtimeOnly(fg.deobf("software.bernie.geckolib:geckolib-forge-${property("minecraft_version")}:${property("geckolib_version")}"))
    runtimeOnly(fg.deobf("top.theillusivec4.caelus:caelus-forge:${property("caelus_version")}"))
    runtimeOnly(fg.deobf("dev.kosmx.player-anim:player-animation-lib-forge:${property("player_animator_version")}"))
    runtimeOnly(fg.deobf("curse.maven:entityjs-967617:${property("entityjs_file_id")}"))

    runtimeOnly(fg.deobf("mezz.jei:jei-${property("jei_mc_version")}-forge:${property("jei_version")}"))

    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

    val mixinExtrasForgeDep = jarJar("io.github.llamalad7:mixinextras-forge:0.4.1")
        ?: error("jarJar() returned null for mixinextras-forge")

    implementation(mixinExtrasForgeDep)
    jarJar.ranged(mixinExtrasForgeDep, "[0.4.1,)")

    implementation("org.reflections:reflections:0.10.2")
    implementation("org.javassist:javassist:3.20.0-GA")
}


val resourceTargets = listOf("META-INF/mods.toml", "pack.mcmeta")

val replaceProperties: Map<String, String> = mapOf(
    "minecraft_version" to property("minecraft_version").toString(),
    "minecraft_version_range" to property("minecraft_version_range").toString(),
    "forge_version" to property("forge_version").toString(),
    "forge_version_range" to property("forge_version_range").toString(),
    "loader_version_range" to property("loader_version_range").toString(),
    "mod_id" to property("mod_id").toString(),
    "mod_name" to property("mod_name").toString(),
    "mod_license" to property("mod_license").toString(),
    "mod_version" to property("mod_version").toString(),
    "mod_authors" to property("mod_authors").toString(),
    "mod_description" to property("mod_description").toString(),
    "entityjs_version_range" to property("entityjs_version_range").toString(),
    "curios_version_range" to property("curios_version_range").toString(),
    "irons_spellbooks_version_range" to property("irons_spellbooks_version_range").toString()
)

tasks.processResources {
    inputs.properties(replaceProperties)
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

val modId = providers.gradleProperty("mod_id").get()
val modAuthors = providers.gradleProperty("mod_authors").get()
val modVersion = providers.gradleProperty("mod_version").get()

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modId,
                "Specification-Vendor" to modAuthors,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to modVersion,
                "Implementation-Vendor" to modAuthors,
                "Implementation-Timestamp" to JSdf("yyyy-MM-dd'T'HH:mm:ssZ").format(JDate())
            )
        )
    }
}


tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.named("jar") {
    finalizedBy("reobfJar")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.jar.get())
        }
    }
    repositories {
        maven {
            url = uri("file://${project.projectDir}/mcmodsrepo")
        }
    }
}
