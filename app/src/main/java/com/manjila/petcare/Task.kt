package com.manjila.petcare

data class Task(
    val id: Int,
    val petId: Int,
    val taskName: String,
    val frequency: String,
    val taskTime: String?,
    val supplies: String?,
    val notes: String?,
    val completed: Boolean
)