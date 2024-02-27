package com.ilham.etumarketbybuyer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ilham.etumarketbybuyer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
//    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomnav.setupWithNavController(navController)




        binding.bottomnav.setOnItemSelectedListener { item ->

            when(item.itemId){
                R.id.homeFragment2 -> {
                    navController.navigate(R.id.homeFragment2)
                    true
                }
                R.id.cartFragment -> {
                    navController.navigate(R.id.cartFragment)
                    true
                }
                R.id.historyFragment2 -> {
                    navController.navigate(R.id.historyFragment2)
                    true
                }

                R.id.profileFragment2 ->{
                    navController.navigate(R.id.profileFragment2)
                    true
                }
                R.id.userFragment->{
                    navController.navigate(R.id.userFragment)
                    true
                } else -> {
                false
            }
            }

        }



        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.registerFragment  -> {
                    binding.bottomnav.visibility = View.GONE
                }
                else -> {
                    binding.bottomnav.visibility = View.VISIBLE
                }
            }
        }







    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d("Main","ON Destroy")
    }
}