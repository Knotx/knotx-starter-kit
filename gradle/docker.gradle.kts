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
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.bmuschko:gradle-docker-plugin:4.9.0")
    }
}

val dockerImageRef = "$buildDir/.docker/buildImage-imageId.txt"
val dockerImageName = "knotx/knotx-starter-kit"

tasks.create("removeImage", DockerRemoveImage::class) {
    group = "docker"

    val spec = object : Spec<Task> {
        override fun isSatisfiedBy(task: Task): Boolean {
            return File(dockerImageRef).exists()
        }
    }
    onlyIf(spec)

    targetImageId(if (File(dockerImageRef).exists()) File(dockerImageRef).readText() else "")
    dependsOn("clean")
}

tasks.create("buildImage", DockerBuildImage::class) {
    group = "docker"
    description = "Build project Docker image."

    inputDir.set(file("$buildDir"))
    tags.add("$dockerImageName:latest")

    dependsOn("copyModulesWithDeps", "copyConfigs", "copyDockerfile")
}