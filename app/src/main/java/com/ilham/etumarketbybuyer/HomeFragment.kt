package com.ilham.etumarketbybuyer

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.doOnAttach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbybuyer.databinding.FragmentHomeBinding
import com.ilham.etumarketbybuyer.model.dataslider.DataSliderResponse
import com.ilham.etumarketbybuyer.viewmodel.CartViewModel
import com.ilham.etumarketbybuyer.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var productVm : ProductViewModel
    lateinit var CartVm : CartViewModel
    private lateinit var kelasList:ArrayList<DataSliderResponse>
    private lateinit var sliderAdapter:SliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        CartVm = ViewModelProvider(this).get(CartViewModel::class.java)

        getProduct()

        kelasList = ArrayList()
        kelasList.add(DataSliderResponse(R.drawable.banner_home))
        kelasList.add(DataSliderResponse(R.drawable.kena))
        kelasList.add(DataSliderResponse(R.drawable.second_onboarding))


        binding.viewPager.apply {
            sliderAdapter = SliderAdapter(kelasList)
            adapter = sliderAdapter
            binding.dotIndicatorSlide.setViewPager2(binding.viewPager)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

    }

    fun getProduct(){
        productVm.getAllproduct()
        productVm.dataProduct.observe(viewLifecycleOwner, Observer{
            val layoutManager = GridLayoutManager(context,2)
            binding.rvProductHome.layoutManager = layoutManager
            if (it!= null) {
                binding.rvProductHome.adapter = BuyerAdapter(it)
            }
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Boolean {
//        inflater.inflate(R.menu.search_menu, menu)
//
//        //Create search Manager
//        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val vSearch = menu?.findItem(R.id.search_menu)?.actionView as SearchView
//
//        vSearch.apply {
//            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
//            queryHint = resources.getString(R.string.hint)
//            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    if (query != null) {
//                        userVm.userSearch(query)
//                    }
//
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    return false
//                }
//
//            })
//        }
//        return true
//    }




}