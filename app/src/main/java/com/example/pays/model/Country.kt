package com.example.pays.model

data class Country(
    val name : Name,
    val flags : Flag,
    val capital : List<String>?,
    val population : Long,
    val continents : List<String>
)

data class Name(
    val common : String
)
data class Flag(
    val png : String
)