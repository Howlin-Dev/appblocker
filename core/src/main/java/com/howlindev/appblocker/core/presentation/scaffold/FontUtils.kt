package com.howlindev.appblocker.core.presentation.scaffold

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import com.howlindev.appblocker.core.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

fun getGoogleFontFamily(
    fontName: String,
    provider: GoogleFont.Provider = com.howlindev.appblocker.core.presentation.scaffold.provider,
): FontFamily {
    val googleFont = GoogleFont(fontName)
    return FontFamily(
        Font(googleFont = googleFont, fontProvider = provider)
    )
}
