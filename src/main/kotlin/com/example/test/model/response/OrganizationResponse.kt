package com.example.test.model.response

import java.util.UUID

data class OrganizationResponse(
    val id: UUID,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String?,
    val ownerId: UUID,
    val isActive: Boolean
)
