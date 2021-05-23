package com.tedilabs.voca.repository

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAll(): Single<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id > :id LIMIT :count")
    fun getNext(id: Int, count: Int): Single<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id < :id ORDER BY id DESC LIMIT :count")
    fun getPrev(id: Int, count: Int): Single<List<WordEntity>>

    @Query("SELECT COUNT(id) FROM words")
    fun getCount(): Int
}
