package com.example.testtubesmanual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel :ViewModel() {
    private var query:MutableLiveData<String> = MutableLiveData<String>()
    fun setQuery(queryData:String){
        query.value = queryData
    }
    fun getQuery():LiveData<String> {
        return query
    }
}