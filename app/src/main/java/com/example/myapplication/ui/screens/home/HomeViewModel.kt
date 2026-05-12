package com.example.myapplication.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Service
import com.example.myapplication.data.ServiceRepository

class HomeViewModel : ViewModel() {

    val services: List<Service> = ServiceRepository.services
}