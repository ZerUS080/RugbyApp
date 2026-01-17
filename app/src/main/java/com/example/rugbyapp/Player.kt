package com.example.rugbyapp

data class Player(
    val id: Int,
    val name: String,
    val birthDate: String,
    val position: String,
    val nationality: String,
    val currentTeam: String,
    val imageName: String,
    val imageUrl: String = ""
)
