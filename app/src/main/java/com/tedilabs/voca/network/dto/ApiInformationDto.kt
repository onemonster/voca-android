package com.tedilabs.voca.network.dto

data class ApiInformationDto(
    val name: String,
    val version: String,
    val description: String,
    val clients: ClientsDto,
    val environments: EnvironmentsDto
) {

    data class ClientsDto(
        val android: AndroidClientDto
    ) {
        data class AndroidClientDto(
            val latest: String,
            val required: String
        )
    }

    data class EnvironmentsDto(
        val nodeVersion: String,
        val hostname: String,
        val platform: String
    )
}
