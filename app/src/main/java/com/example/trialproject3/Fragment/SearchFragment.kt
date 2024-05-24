package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Adapter.ProductsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentSearchBinding
import java.util.Locale

class SearchFragment : Fragment(), MainActivity.OnBackPressedListener {
    private lateinit var binding: FragmentSearchBinding
    private val TAG = "SearchFragment"
    private var selectedCategory: String? = null

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchBar.requestFocus()

        binding.backArrow.setOnClickListener { backToHomeFragment() }

        binding.searchBtn.setOnClickListener {
            if (binding.searchBar.text.toString().isNotEmpty())
                if (selectedCategory != null)
                    searchProductsByCategory(selectedCategory!!)
                else
                    searchProducts(binding.searchBar.text.toString())
            else
                return@setOnClickListener

        }

        val productCategorySpinner = binding.productCategorySpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            selectedCategory = productCategorySpinner.selectedItem?.toString()
            productCategorySpinner.adapter = adapter
        }
        productCategorySpinner.setSelection(0)
        productCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = parent.getItemAtPosition(position).toString()
                    if (position != 0)
                        binding.searchBar.setText("Category: $selectedCategory")

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

        binding.searchBar.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                if (binding.searchBar.text.toString().isNotEmpty())
                    if (selectedCategory != null)
                        searchProductsByCategory(selectedCategory!!)
                    else
                        searchProducts(binding.searchBar.text.toString())
                searchProducts(binding.searchBar.text.toString())
                true
            } else
                false

        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start are about to be replaced by new text with length after.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start have just replaced old text that had length before.
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
                if (s.toString().isEmpty()) {
                }

            }
        })

        return binding.root
    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
        return true
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun searchProducts(query: String) {
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
            .whereEqualTo("productNameLowercase", query.toLowerCase(Locale.getDefault()))

        productReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val productsList = mutableListOf<ProductsModel>()
                    for (document in task.result) {
                        val product = document.toObject(ProductsModel::class.java)
                        productsList.add(product)
                        Log.d(TAG, "searchProducts: $productsList")
                    }

                    val bundle = Bundle()
                    bundle.putSerializable("productsList", ArrayList(productsList))
                    val homeFragment = HomeFragment()
                    homeFragment.arguments = bundle

                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else
                    Log.e(TAG, "Error getting products: ", task.exception)

            }
    }

    private fun searchProductsByCategory(query: String) {
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
            .whereEqualTo("productCategory", query.toLowerCase(Locale.getDefault()))

        productReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val productsList = mutableListOf<ProductsModel>()
                    for (document in task.result) {
                        val product = document.toObject(ProductsModel::class.java)
                        productsList.add(product)
                    }

                    val bundle = Bundle()
                    bundle.putSerializable("productsList", ArrayList(productsList))
                    val homeFragment = HomeFragment()
                    homeFragment.arguments = bundle

                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

//                val productsAdapter = ProductsAdapter(context, productsList, this@HomeFragment)
//                binding.productsRecyclerView.layoutManager = LinearLayoutManager(context)
//                binding.productsRecyclerView.adapter = productsAdapter
                } else {
                    Log.e(TAG, "Error getting products: ", task.exception)
                }
            }
    }

}