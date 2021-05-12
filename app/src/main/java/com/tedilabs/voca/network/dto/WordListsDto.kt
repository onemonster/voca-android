package com.tedilabs.voca.network.dto

data class WordListsDto(
    val metadata: MetadataDto,
    val data: List<WordListDto>
) {

    data class MetadataDto(val total: Long)

    data class WordListDto(
        val name: String,
        val sourceLanguage: String,
        val targetLanguage: String,
        val version: String,
        val url: String
    )
}
