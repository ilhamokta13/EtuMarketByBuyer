package com.ilham.etumarketbybuyer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilham.etumarketbybuyer.database.BuyerDatabase
import com.ilham.etumarketbybuyer.database.FavoriteBuyer


import kotlinx.coroutines.*
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(app:Application) : AndroidViewModel(app) {
    private var liveDatalistCart: MutableLiveData<List<FavoriteBuyer>> = MutableLiveData()


    fun getliveDataCartfav(): MutableLiveData<List<FavoriteBuyer>> {
        return  liveDatalistCart
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun getAllCartPopular() {

        GlobalScope.launch {
            val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
            val listCart = dataDao.getFavorit()
            liveDatalistCart.postValue(listCart)

        }
    }

    suspend fun delete(favoritBuyer : FavoriteBuyer) {
        val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
        dataDao.deleteBuyerFavorites(favoritBuyer)
        getAllCartPopular()
    }

    suspend fun insert(favoritMovie : FavoriteBuyer){
        val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
        dataDao.addToFavorit(favoritMovie)
        getAllCartPopular()
    }


    fun check(id: String){
        val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
        dataDao.checkBuyer(id)
        getAllCartPopular()
    }


}