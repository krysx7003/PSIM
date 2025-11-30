package com.napnap.heartbridge

import kotlinx.serialization.Serializable

@Serializable
data class Measurement(
    val id:Int,
    val date: String,
    val hour: String,
    val bpm: String,
    val device: String
)