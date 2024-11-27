package com.jcortes.deco.tools.util.infrastructure

import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.ImageNameSubstitutor

class ContainerImageNameSubstitutor : ImageNameSubstitutor() {

    private val pgVectorDockerImage = DockerImageName
        .parse("pgvector/pgvector")
        .asCompatibleSubstituteFor("postgres")

    override fun apply(original: DockerImageName): DockerImageName {

        if (original.asCanonicalNameString().contains("postgres")) {
            return pgVectorDockerImage
        }

        return original
    }

    override fun getDescription(): String {
        return "Substitute postgres image with pgvector image"
    }
}
