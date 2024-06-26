package com.ilham.etumarketbybuyer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteBuyerDao {
    @Insert
     suspend fun addToFavorit(favorite : FavoriteBuyer) : Long

    @Query("SELECT * FROM FavoriteBuyer")
    fun getFavorit() : List<FavoriteBuyer>

    @Query("SELECT count(*) FROM FavoriteBuyer WHERE FavoriteBuyer.id = :id")
    fun checkBuyer(id: String) : String

    @Delete
    suspend fun deleteBuyerFavorites(BuyerFavorites: FavoriteBuyer) : Int
}