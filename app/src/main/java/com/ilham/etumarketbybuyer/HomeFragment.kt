package com.ilham.etumarketbybuyer

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
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
    lateinit var adapter : BuyerAdapter

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


        val query = binding.etSearchProduct.text.toString()
        getProduct(query)


        adapter = BuyerAdapter(ArrayList())

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

        binding.etSearchProduct.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearchProduct.text.toString()
                getProduct(query)
                return@setOnEditorActionListener true
            }
            false
        }

    }


    fun getProduct(search:String){
        productVm.getAllproduct(search)
        productVm.dataProduct.observe(viewLifecycleOwner, Observer{
            val layoutManager = GridLayoutManager(context,2)
            binding.rvProductHome.layoutManager = layoutManager
            if (it!= null) {
                binding.rvProductHome.adapter = BuyerAdapter(it)
            }
        })
    }








}