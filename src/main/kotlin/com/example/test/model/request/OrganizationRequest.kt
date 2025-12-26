package com.example.test.model.request

data class OrganizationRequest(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String?
)
