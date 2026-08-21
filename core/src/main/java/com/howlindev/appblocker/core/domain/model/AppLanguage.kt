package com.howlindev.appblocker.core.domain.model

enum class AppLanguage(
    val tag: String,
    val title: String,
) {
    ENGLISH("en-US", "English"),
    UKRAINIAN("uk-UA", "Українська"),
    SPANISH("es-ES", "Español"),
    CZECH("cs-CZ", "Český"),
    RUSSIAN("ru-RU", "Русский"),
}
