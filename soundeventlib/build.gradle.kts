plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "io.github.eescobar.soundeventlib"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.GuitHub75"
                artifactId = "sound-event-lib"
                version = libs.versions.soundeventlib.get()

                pom {
                    name = "SoundEventLib"
                    description = "Lightweight Android library for playing sounds in response to named application events."
                    url = "https://github.com/GuitHub75/sound-event-lib"

                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/licenses/MIT"
                        }
                    }

                    developers {
                        developer {
                            id = "GuitHub75"
                            name = "Erick Escobar"
                            email = "eescobar2500@gmail.com"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/GuitHub75/sound-event-lib.git"
                        developerConnection = "scm:git:ssh://github.com/GuitHub75/sound-event-lib.git"
                        url = "https://github.com/GuitHub75/sound-event-lib"
                    }
                }
            }
        }
    }
}
