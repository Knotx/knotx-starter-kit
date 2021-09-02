/*
 * Copyright (C) 2019 Knot.x Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("io.knotx.distribution")
    id("io.knotx.release-base")
    id("com.bmuschko.docker-remote-api")
    id("java")
}

dependencies {
    subprojects.forEach { "dist"(project(":${it.name}")) }
}

sourceSets.named("test") {
    java.srcDir("functional/src/test/java")
}

allprojects {
    group = "com.project"

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://oss.sonatype.org/content/groups/staging/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    pluginManager.withPlugin("java") {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(8))
                vendor.set(JvmVendorSpec.ADOPTOPENJDK)
            }
        }
    }
}

tasks.named("build") {
    dependsOn("build-stack")
}

tasks.register("build-docker") {
    group = "docker"
    dependsOn("runFunctionalTest")
}

tasks.register("build-stack") {
    group = "stack"
    // https://github.com/Knotx/knotx-gradle-plugins/blob/master/src/main/kotlin/io/knotx/distribution.gradle.kts
    dependsOn("assembleCustomDistribution")
    mustRunAfter("build-docker")
}

/** Knot.x releasing, can be removed **/

tasks {
    named<io.knotx.release.common.ProjectVersionUpdateTask>("setVersion") {
        group = "release prepare"
        versionParamProperty = "knotxVersion"
        propertyKeyNameInFile = "knotxVersion"
    }

    named("build-docker") {
        mustRunAfter("setVersion")
    }

    named("updateChangelog") {
        dependsOn("build-docker", "setVersion")
    }

    register("prepare") {
        group = "release"
        dependsOn("updateChangelog")
    }

    register("publishArtifacts") {
        group = "release"
        logger.lifecycle("Starter-kit does not publish anything")
    }
}

apply(from = "gradle/javaAndUnitTests.gradle.kts")
apply(from = "gradle/docker.gradle.kts")

