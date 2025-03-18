
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginRepositoryUrl: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project

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

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        androidStudio(
            "2024.2.2.13"
        ) // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html#2024
        bundledPlugins("org.jetbrains.kotlin", "com.intellij.gradle")
        plugin("org.jetbrains.android:243.23654.153")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("reflect"))

    testImplementation(libs.junit)

    detektPlugins(libs.detekt.formatting)
}

intellijPlatform {
    pluginConfiguration {
        name = pluginName
        group = pluginGroup
        version = pluginVersion

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
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = providers.gradleProperty("pluginVersion")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

changelog {
    version = pluginVersion
    repositoryUrl = pluginRepositoryUrl
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/codeQuality/detekt.yml")
}

val runLocalIde by intellijPlatformTesting.runIde.registering {
    localPath.set(file("/Applications/Android Studio.app/Contents"))
}
