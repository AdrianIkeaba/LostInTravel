# 🌍 LostInTravel Android App

A lightweight, modern Android app for exploring curated travel destinations. Built entirely with **Jetpack Compose** for a seamless, declarative UI experience.

## 🚀 Features

- Browse visually-rich destination cards
- Optimized for performance and simplicity
- Built with modern Android libraries
- Clean and reusable composables

## 🛠️ Set up 🛠️  
To run this application you would need;

- Stable internet connection
- [Android Studio](https://developer.android.com/studio) & [Xcode](https://apps.apple.com/us/app/xcode/id497799835) installed

### Getting things ready.
- Clone this repo:
  ```shell
    git clone https://github.com/AdrianIkeaba/LostInTravel
  ```

### Starting
1. Open the folder in Android Studio, and wait for all dependencies to be installed.
2. Create a new emulator if you haven't already [Create an emulator](https://developer.android.com/studio/run/managing-avds).
3. In your configurations list select `app`.
4. Select your preferred virtual device and click **Run**
5. To run on a physical Android device, enable `Wireless debugging` or `USB debugging`. [More info here](https://developer.android.com/studio/run/device)


## 🧱 Architecture & Design Choices
### Tech STACK
- Kotlin
- Jetpack Compose
- ApolloGraphQL
- Material3
- Coil
- Coroutines
- Datastore (caching & offline support)
- Koin (lightweight dependency injection)
- Compose Navigation
- Google Services Location

### Design Choices
- MVVM (Model View ViewModel) approach was followed to seperate logic from UI
  
- Card-based UI: Prioritizes destination imagery with minimal layout noise

- Composable-driven architecture: Encourages separation of concerns

- Performance-conscious choices: Lightweight build and fast image loading

- Caching Support: Allows users view data even when offline.

## 🚧 Future Improvements
-  Connect to backend (e.g., Supabase or Firebase)

-  Implement sign-up / login system with Google, Facebook and/or X/Twitter

-  Add detail screens for each location

-  Allow users to favorite or bookmark places

-  Enable dark theme support

-  Make layout responsive for tablets.

-  Add loading skeletons and enhanced animations.
