// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka)
}

allprojects {
    group = "com.alexfu.axiomui"
    version = "0.3.0"

    plugins.apply("maven-publish")

    afterEvaluate {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/alexfu/AxiomUI")
                    credentials {
                        val env = System.getenv()
                        username = env["GITHUB_ACTOR"]
                        password = env["GITHUB_TOKEN"]
                    }
                }
            }
        }
    }
}

dependencies {
    dokka(project(":state"))
    dokka(project(":command"))
    dokka(project(":viewmodel"))
}
