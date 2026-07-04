package com.turkcell.rencar.feature.vehicles.domain.model

data class Vehicle(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val pricePerDay: Double,
    val latitude: Double,
    val longitude: Double
)
