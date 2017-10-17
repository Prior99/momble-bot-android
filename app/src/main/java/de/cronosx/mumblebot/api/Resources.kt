package de.cronosx.mumblebot.api

import java.util.*

class Settings (
    val record: Boolean
)

class User (
    val minecraft: String,
    val username: String,
    val id: Int,
    val steamdi: String,
    val money: Int,
    val settings: Settings
)

class Label (
    val name: String,
    val id: Int,
    val recordings: Int
)

class Recording (
    val changed: Date,
    val duration: Double,
    val id: Int,
    val labels: List<Int>,
    val overwrite: Boolean,
    val parent: Int?,
    val quote: String,
    val reporter: Int,
    val submitted: Date,
    val used: Int,
    val user: Int
)

class Sound (
    val id: Int,
    val name: String,
    val used: Int
)