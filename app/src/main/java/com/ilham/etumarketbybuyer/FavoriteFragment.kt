package com.ilham.etumarketbybuyer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.ilham.etumarketbybuyer.databinding.FragmentFavoriteBinding
import com.ilham.etumarketbybuyer.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbybuyer.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
  lateinit var binding : FragmentFavoriteBinding
    private lateinit var buyerAdapter: BuyerAdapter
    private val favoriteViewModel: FavoriteViewModel by viewModels()
//    private var mDBnew: BuyerDatabase? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buyerAdapter = BuyerAdapter(ArrayList())
//        mDBnew = BuyerDatabase.getInstance(requireContext())

//       buyerAdapter.onClickArt = {
//            val bundle = Bundle().apply {
//                putSerializable("detail", it)
//
//
//
//            }
//
//           findNavController().navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
//
//        }

    }


//    override fun onResume() {
//        super.onResume()
//        favoriteViewModel.getAllBuyer()
//        favoriteViewModel.getliveData().observe(this) { users ->
//            users?.let {
//                val data = favoriteViewModel.getAllBuyer()
//                buyerAdapter.setDataUser(data)
//                binding.rvFav.adapter = buyerAdapter
//                val layoutManager = GridLayoutManager(context, 2)
//                binding.rvFav.layoutManager = layoutManager
//
//            }
//        }
//    }









        }



//    private fun listFavorite(users: List<FavoriteBuyer>): ArrayList<DataAllProduct> {
//        val listUsers = ArrayList<DataAllProduct>()
//        for (user in users){
//            val usersMap = DataAllProduct(
//
//                user.category, user.description, user.id.toString(),user.image.toString(), user.latitude, user.longitude, user.nameProduct, user.price, user.releaseDate, user.sellerID , 0
//
//
//            )
//            listUsers.add(usersMap)
//        }
//        return listUsers
//
//
//
//
//    }

