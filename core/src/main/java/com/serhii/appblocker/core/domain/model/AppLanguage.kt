package com.serhii.appblocker.core.domain.model

enum class
AppLanguage(
    val tag: String? = null,
    val title: String? = null,
) {
    SYSTEM,
    ENGLISH("en", "English"),
    UKRAINIAN("uk", "Українська"),
    SPANISH("es", "Español"),
    CZECH("cs", "Český"),
    RUSSIAN("ru", "Русский"),
}
