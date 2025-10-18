package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



class SearchViewModel : ViewModel(){
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChanged(newValue:String){
        _query.value = newValue
    }


}