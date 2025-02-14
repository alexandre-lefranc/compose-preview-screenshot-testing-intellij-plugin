import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.*

val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        androidStudio("2024.2.2.13") // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html#2024
        bundledPlugins("org.jetbrains.kotlin", "com.intellij.gradle")
        plugin("org.jetbrains.android:243.23654.153")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
}

intellijPlatform {
    pluginConfiguration {
        name = pluginName
        group = pluginGroup
        version = platformVersion

//        description = ""
        changeNotes = changelog.renderItem(
            changelog
                .getUnreleased()
                .withHeader(false)
                .withEmptySections(false),
            HTML
        )

        ideaVersion {
            sinceBuild = pluginSinceBuild
            untilBuild = pluginUntilBuild
        }
    }
    signing {
//        certificateChainFile = file(localProperties.getProperty("certificateChainFile"))
//        privateKeyFile = file(localProperties.getProperty("privateKeyFile"))
//        password = localProperties.getProperty("password")
    }
    publishing {
        //token = localProperties.getProperty("token")
        //channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }
}

changelog {
    version = pluginVersion
    repositoryUrl = "https://github.com/pbreault/adb-idea"
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/codeQuality/detekt.yml")
}

val runLocalIde by intellijPlatformTesting.runIde.registering {
    localPath.set(file("/Applications/Android Studio.app/Contents"))
}
