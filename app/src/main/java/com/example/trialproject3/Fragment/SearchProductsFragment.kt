package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Toast
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentSearchProductsBinding
import java.util.Locale

class SearchProductsFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = "SearchFragment"
    private lateinit var binding: FragmentSearchProductsBinding
    private lateinit var context: Context
    private var selectedCategory: String? = null

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchProductsFragment().apply {
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
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)

        binding.searchBar.requestFocus()

        context = requireContext()

        binding.backArrow.setOnClickListener { backToHomeFragment() }

        val productCategorySpinner = binding.productCategorySpinner
        val postCategories = resources.getStringArray(R.array.product_categories)
        val adapter = object : ArrayAdapter<String>(
            context,
            R.layout.spinner_item,
            postCategories
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item)
        productCategorySpinner.adapter = adapter

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
                    if (binding.searchBar.text.toString().startsWith("Category: "))
                        searchProductsByCategory(selectedCategory!!)
                    else searchProducts(binding.searchBar.text.toString())
                true
            } else
                false
        }

        binding.searchBtn.setOnClickListener {
            if (binding.searchBar.text.toString().isNotEmpty())
                if (binding.searchBar.text.toString().startsWith("Category: "))
                    searchProductsByCategory(selectedCategory!!)
                else
                    searchProducts(binding.searchBar.text.toString())
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
            .whereArrayContains("productNameWords", query.toLowerCase(Locale.getDefault()))

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
        Toast.makeText(context, "Searching by category: $query", Toast.LENGTH_SHORT).show()
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
            .whereEqualTo("productCategory", query)

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