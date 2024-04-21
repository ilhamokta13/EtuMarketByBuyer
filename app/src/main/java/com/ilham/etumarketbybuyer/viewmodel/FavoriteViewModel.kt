package com.ilham.etumarketbybuyer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.*

class FavoriteViewModel(application: Application): AndroidViewModel(application) {
//    private var liveDatalistCart: MutableLiveData<List<FavoriteBuyer>> = MutableLiveData()
//
//
//
//    init {
//        getAllBuyer()
//    }
//
//
//    fun getliveData(): MutableLiveData<List<FavoriteBuyer>> {
//        return  liveDatalistCart
//    }
//
//    @OptIn(DelicateCoroutinesApi::class)
//    fun getAllBuyer() {
//
//        GlobalScope.launch {
//            val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
//            val listNote = dataDao.getFavorit()
//            liveDatalistCart.postValue(listNote)
//
//        }
//
//
//    }
//
//    fun addFavorite(favoritUser: FavoriteBuyer) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
//                dataDao.addToFavorit(favoritUser)
//            }
//            getAllBuyer()
//        }
//    }
//
//
//    fun removeFavorite(favoritUser : FavoriteBuyer){
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
//            dataDao.deleteBuyerFavorites(favoritUser)
//        }
//
//        getAllBuyer()
//
//    }
//
//    fun checkFavorite(id: String): String {
//        val dataDao = BuyerDatabase.getInstance(getApplication())!!.buyerDao()
//        return dataDao.checkBuyer(id)
//
//    }

}