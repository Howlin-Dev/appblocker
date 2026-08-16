# Implementation Plan - Language Settings Refinement and Xiaomi Fix

This plan addresses the reported issue with language switching on Xiaomi devices and implements the suggestion to remove the "System" option from the language settings.

## User Review Required

> [!IMPORTANT]
> Removing the **System** option means the app will no longer automatically follow changes to the device's system language after the initial setup. The app will default to a language matching the system locale (if supported) or English on the first run, and then stay on that language until changed by the user.

## Proposed Changes

### Core Module

#### [MODIFY] [AppLanguage.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/core/src/main/java/com/howlindev/appblocker/core/domain/model/AppLanguage.kt)
- Remove `SYSTEM` entry.
- Make `tag` and `title` non-nullable.

#### [MODIFY] [SettingsData.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/core/src/main/java/com/howlindev/appblocker/core/domain/model/SettingsData.kt)
- Update default language to a sensible default (handled in repository).

---

### Settings Module

#### [MODIFY] [SettingsRepositoryImpl.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/settings/src/main/java/com/howlindev/appblocker/settings/data/repository/SettingsRepositoryImpl.kt)
- Update the default language logic to detect the system locale and match it against supported languages, defaulting to `ENGLISH` if no match is found.

#### [MODIFY] [LanguageViewModel.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/settings/src/main/java/com/howlindev/appblocker/settings/presentation/language/LanguageViewModel.kt)
- Simplify `setLanguage` to remove `SYSTEM` handling.

#### [MODIFY] [LanguageScreen.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/settings/src/main/java/com/howlindev/appblocker/settings/presentation/language/LanguageScreen.kt)
- Remove fallback text for `SYSTEM` in `LanguageItem`.

---

### App Module

#### [MODIFY] [RootScreen.kt](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/app/src/main/java/com/howlindev/appblocker/presentation/root/RootScreen.kt)
- Simplify language application logic.
- Add extra robustness for Xiaomi devices: ensure `setApplicationLocales` is called correctly and consider adding a small check if the activity needs manual recreation (though `AppCompatDelegate` should handle this).

#### [MODIFY] [locales_config.xml](file:///C:/Users/serhi/AndroidStudioProjects/AppBlocker/app/src/main/res/xml/locales_config.xml)
- Ensure it matches the supported tags (no changes needed here, but I'll double-check).

## Verification Plan

### Automated Tests
- Verify that `SettingsRepositoryImpl` correctly maps system locales to supported `AppLanguage` entries.
- Verify that `AppLanguage` serialization still works correctly.

### Manual Verification
- Deploy to a Xiaomi device (if available, otherwise rely on logic improvements).
- Change language in Settings and verify the app UI updates immediately.
- Restart the app and verify the selected language is persisted.
- Verify that the "System" option is gone from the UI.
