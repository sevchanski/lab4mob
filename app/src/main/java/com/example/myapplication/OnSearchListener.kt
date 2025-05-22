package com.example.myapplication
import com.example.myapplication.model.SearchData


interface OnSearchListener {
    fun onSearchSubmitted(data: SearchData)
}
