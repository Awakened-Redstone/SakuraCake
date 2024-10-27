@file:Suppress("UnstableApiUsage")

import java.net.URI


//region Setup
plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    id("dev.kikugie.j52j") version "1.0.2"
}

class CompatMixins {
    private var common: List<String> = listOf()
    private var fabric: List<String> = listOf()
    private var neoforge: List<String> = listOf()

    fun getMixins(): Map<String, String> {
        val mixins = common + if (loader.isFabric) fabric else neoforge
        return mapOf(
            "mod_id" to mod.id,
            "compat_mixins" to "[\n${mixins.joinToString(",\n") { "\"$it\"" }}\n]"
        )
    }
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = MinecraftVersionData(stonecutter)
val awName = "${mod.id}.accesswidener"

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.id)
//endregion

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://maven.shedaniel.me")
    maven("https://jitpack.io")
    maven("https://maven.su5ed.dev/releases")

    maven("https://maven.felnull.dev")

    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = URI("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

    maven {
        setUrl("https://dl.cloudsmith.io/public/klikli-dev/mods/maven/")
        content {
            includeGroup("com.klikli_dev")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    annotationProcessor(modImplementation("com.bawnorton.configurable:configurable-${loader.name()}-yarn:${property("configurable")}+$minecraftVersion") {
        exclude(module = "fabric-networking-api-v1")
        exclude(module = "yet-another-config-lib")
    })

    modCompileOnly("com.klikli_dev:modonomicon-$minecraftVersion-${loader.name()}:${property("modonomicon")}") {
        exclude("mezz.jei")
    }

    //modCompileOnly("maven.modrinth:sodium:mc1.21-0.6.0-beta.2-${loader.name()}")
}

fabric {
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")

    include(implementation(annotationProcessor("io.github.llamalad7:mixinextras-fabric:0.5.0-beta.3")!!)!!)
    include(modApi("dev.felnull:special-model-loader:1.3.0") {
        exclude("net.fabricmc.fabric-api")
    })
}

neoforge {
    modImplementation("org.sinytra.forgified-fabric-api:forgified-fabric-api:${property("fabric_api")}+${property("forgified_fabric_api")}+$minecraftVersion")
}

//region Build
fun Project.fabric(configuration: DependencyHandlerScope.() -> Unit) {
    if (loader.isFabric) dependencies(configuration)
}

fun Project.neoforge(configuration: DependencyHandlerScope.() -> Unit) {
    if (loader.isNeoForge) dependencies(configuration)
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$awName"))

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        vmArgs("-Dmixin.debug.export=true")
        programArgs("--uuid 2e7c2349-94ec-4862-8b68-344d049840d2 --username AwakenedRedstone")
    }

    sourceSets {
        main {
            resources {
                srcDir(project.file("src/main/generated"))
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release = minecraftVersion.javaVersion()
    }

    processResources {
        val compatMixins = CompatMixins().getMixins()
        inputs.properties(compatMixins)
        filesMatching("${mod.id}-compat.mixins.json") { expand(compatMixins) }
    }

    jar {
        dependsOn("copyDatagen")
    }

    withType<AbstractCopyTask> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    /*clean {
        delete(file(rootProject.file("build")))
    }*/
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}
//endregion

//region Others
if (loader.isFabric) {
    fabricApi {
        configureDataGeneration {
            createRunConfiguration = true
            modId = mod.id
            outputDirectory = rootProject.rootDir.resolve("src/main/generated")
        }
    }

    dependencies {
        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
    }

    tasks {
        register<Copy>("copyDatagen") {
            from("src/main/generated")
            into("${layout.buildDirectory.get()}/resources/main")
            dependsOn("runDatagen")
        }

        processResources {
            val modMetadata = mapOf(
                "mod_id" to mod.id,
                "mod_name" to mod.name,
                "description" to mod.description,
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency
            )

            inputs.properties(modMetadata)
            filesMatching("fabric.mod.json") { expand(modMetadata) }
        }
    }
}

if (loader.isNeoForge) {
    val generatedSources = rootProject.rootDir.resolve("src/main/generated")

    sourceSets {
        main {
            resources.srcDir(generatedSources)
        }
    }

    dependencies {
        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
        })
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "mod_id" to mod.id,
                "mod_name" to mod.name,
                "description" to mod.description,
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "loader_version" to loader.getVersion()
            )

            inputs.properties(modMetadata)
            filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
        }

        remapJar {
            atAccessWideners.add(awName)
        }

        register<Copy>("copyDatagen") {
            from(rootProject.file("versions/$minecraftVersion-fabric/src/main/generated"))
            into("${layout.buildDirectory.get()}/resources/main")
        }
    }
}

tasks.register("swapLoaders") {
    group = "stonecutter"

    val tasks = parent!!.tasks
    if (stonecutter.current.isActive) {
        when (loader.name()) {
            "neoforge" -> finalizedBy(tasks.findByName("Set active project to $minecraftVersion-fabric"))
            "fabric" -> finalizedBy(tasks.findByName("Set active project to $minecraftVersion-neoforge"))
        }
    }

    /*if (loader.isNeoForge && stonecutter.current.isActive) {
        finalizedBy(tasks.findByName("Set active project to $minecraftVersion-fabric"))
    } else if (loader.isFabric && stonecutter.current.isActive) {
        finalizedBy(tasks.findByName("Set active project to $minecraftVersion-neoforge"))
    }*/
}
//endregion

//region Publish
extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    val branch = "main"
    changelog = "[Changelog](https://github.com/Awakened-Redstone/${mod.name}/blob/$branch/CHANGELOG.md)"
    displayName =
        "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    /*github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Awakened-Redstone/${mod.name}"
        commitish = branch
        tagName = tag
    }*/

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = mod.modrinthProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }

    /*curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = mod.curseforgeProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }*/
}
//endregion
