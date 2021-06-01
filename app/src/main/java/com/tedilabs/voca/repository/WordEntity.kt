package com.tedilabs.voca.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tedilabs.voca.model.Example
import com.tedilabs.voca.model.Word

@Entity(tableName = "words")
data class WordEntity(
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    @PrimaryKey
    val id: Int?,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val word: String?,

    @ColumnInfo(name = "part_of_speech", typeAffinity = ColumnInfo.TEXT)
    val partOfSpeech: String?,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val pronunciation: String?,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val definitions: String?,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val examples: String?,

    @ColumnInfo(name = "translation_words", typeAffinity = ColumnInfo.TEXT)
    val translationWords: String?
)

fun List<WordEntity>.toWords(moshi: Moshi): List<Word> {
    val definitionsType = Types.newParameterizedType(List::class.java, String::class.java)
    val definitionsAdapter = moshi.adapter<List<String>>(definitionsType)
    val examplesType = Types.newParameterizedType(List::class.java, Example::class.java)
    val examplesAdapter = moshi.adapter<List<Example>>(examplesType)

    return this.map { word ->
        Word(
            id = word.id ?: 0,
            word = word.word ?: "",
            partOfSpeech = word.partOfSpeech ?: "",
            definitions = word.translationWords?.let { definitionsAdapter.fromJson(it) }?.take(3)
                ?: emptyList(),
            pronunciation = word.pronunciation ?: "",
            examples = word.examples?.let { examplesAdapter.fromJson(it) } ?: emptyList(),
        )
    }
}
