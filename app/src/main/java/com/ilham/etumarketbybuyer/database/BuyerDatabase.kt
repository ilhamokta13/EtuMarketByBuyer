package com.ilham.etumarketbybuyer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [FavoriteBuyer::class], version = 1)
abstract class BuyerDatabase : RoomDatabase(){
    abstract fun buyerDao() : FavoriteBuyerDao
    companion object{
        private var INSTANCE : BuyerDatabase? = null

        fun getInstance(context : Context):BuyerDatabase? {
            if (INSTANCE == null){
                synchronized(BuyerDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        BuyerDatabase::class.java,"favoritbuyer.db").build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }

}