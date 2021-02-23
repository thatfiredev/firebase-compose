package io.github.rosariopfernandes.firebasecompose.model

data class Snack(
        val id: Long = 1L,
        val name: String = "Donut",
        val imageUrl: String = "",
        val price: Long = 100L,
        val tagline: String = "Dessert",
)
