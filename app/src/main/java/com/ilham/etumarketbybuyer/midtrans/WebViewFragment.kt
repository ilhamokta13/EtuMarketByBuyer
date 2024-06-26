package com.ilham.etumarketbybuyer.midtrans

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ilham.etumarketbybuyer.R
import com.ilham.etumarketbybuyer.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {
    lateinit var binding : FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebViewBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = arguments?.getString("URL")

        binding.webview.settings.javaScriptEnabled = true // Jika situs memerlukan javascript

        if (url != null) {
            binding.webview.loadUrl(url)
        }
    }
}