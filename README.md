# Bloq

Bloq is a sophisticated Android productivity application designed to help users reclaim their focus by intelligently blocking distracting apps and websites while managing intrusive notifications. 

## Screenshots

<div align="center">
  <img src="screenshots/bloq_1.jpg" width="24%" alt="Profile List" />
  <img src="screenshots/bloq_2.jpg" width="24%" alt="Profile List / Active Profile" />
  <img src="screenshots/bloq_3.jpg" width="24%" alt="Create Profile" />
  <img src="screenshots/bloq_4.jpg" width="24%" alt="Block Screen" />
</div>

## Key Features

-   **Real-time App Blocking**: Utilizes a custom `AccessibilityService` to monitor window state changes and intercept the launch of blacklisted applications instantly.
-   **Advanced Website Interception**: Inspects the UI hierarchy of popular browsers (Chrome, Firefox, etc.) to detect and block access to specific distracting URLs.
-   **Notification Silence**: Employs `NotificationListenerService` to suppress notifications from blocked apps, preventing "notification pull" during focus sessions.
-   **Granular Profiles**: Create and schedule different blocking configurations for work, sleep, or study.

## Architecture & Technical Highlights

Bloq is engineered for scalability and testability using a **Modular Clean Architecture**:

-   **Multi-module Structure**: 
    -   `:app`: Dependency injection root, system service implementations, and navigation;
    -   `:core`: Common utilities and design components;
    -   `:profiles`: Domain and Data layers for managing user-defined blocking configurations;
    -   `:timer`: Scheduling logic;
    -   `:permissions`: Handling permissions;
    -   `:settings`: Application preferences and user data management.

## Tech Stack

-   **Language**: Kotlin
-   **UI**: Jetpack Compose
-   **Architecture**: MVVM / MVI
-   **DI**: Koin
-   **Database**: Room
-   **Persistence**: DataStore
-   **Concurrency**: Coroutines & Flow
-   **Build System**: Gradle Kotlin DSL + Version Catalogs

---

*Bloq is a demonstration of high-quality Android engineering, focusing on solving complex system-level problems with clean, maintainable code.*
