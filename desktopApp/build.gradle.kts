import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "br.upf.ccc.gerenciadorporto.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "br.upf.ccc.gerenciadorporto"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<JavaExec>("runConsole") {
    group = "application"
    description = "Console"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("br.upf.ccc.gerenciadorporto.ConsoleAppKt")

    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}