package com.nikhil.bigbucket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.nikhil.bigbucket.data.Product
import com.nikhil.bigbucket.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _specialProductsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProductsState: StateFlow<Resource<List<Product>>> = _specialProductsState

    private val _bestDealsProductsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProductsState: StateFlow<Resource<List<Product>>> = _bestDealsProductsState

    private val _bestProductsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProductsState: StateFlow<Resource<List<Product>>> = _bestProductsState

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProductsState.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductPage * 10)
                .whereEqualTo("category", "Best Product")
                .get()
                .addOnSuccessListener { result ->
                    val bestProductsList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProductsList == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProductsList
                    viewModelScope.launch {
                        _bestProductsState.emit(Resource.Success(bestProductsList))
                    }
                    pagingInfo.bestProductPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProductsState.emit(Resource.Error(it.message))
                    }
                }
        }
    }

    private fun fetchBestDealsProducts() {
        viewModelScope.launch {
            _bestDealsProductsState.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Best Deals")
            .get()
            .addOnSuccessListener { result ->
                val bestDealsProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProductsState.emit(Resource.Success(bestDealsProductsList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProductsState.emit(Resource.Error(it.message))
                }
            }

    }

    private fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProductsState.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Special Products")
            .get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)

                viewModelScope.launch {
                    _specialProductsState.emit(Resource.Success(specialProductsList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProductsState.emit(Resource.Error(it.message))
                }
            }
    }
}

internal data class PagingInfo(
    var bestProductPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)