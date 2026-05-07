GitHub Repository: https://github.com/djv00/Exercise1.git

Configuration and Run Instructions

1. Clone the repository from GitHub or extract the provided ZIP file.
2. Open the project in Android Studio (Jellyfish or later recommended).
3. Wait for the Gradle sync to complete automatically. (Ensure an active internet connection to download dependencies like androidx.navigation:navigation-compose).
4. Select an Android Emulator or a connected physical Android device.
5. Click the "Run 'app'" button to build and launch the application.

Project Structure

The project follows standard Android Jetpack Compose architecture:
- app/src/main/java/.../MainActivity.kt: Contains the entry point and all Composable functions (ShoeShopApp, HomeScreen, ShoeDetailScreen, CartScreen).
- app/src/main/res/drawable/: Contains all the transparent shoe image assets used in the application.
- build.gradle.kts (Module :app): Contains necessary dependencies, including Material3, Compose Navigation, and Material Icons Extended.

Application Screenshots
<img width="476" height="951" alt="Screenshot 2026-05-06 223048" src="https://github.com/user-attachments/assets/0e2b0292-bd48-40e6-866f-9c2f8ddc7879" />
<img width="432" height="907" alt="Screenshot 2026-05-06 223105" src="https://github.com/user-attachments/assets/35d63e5c-4a8c-4151-8acc-90f270042556" />
<img width="417" height="926" alt="Screenshot 2026-05-06 223127" src="https://github.com/user-attachments/assets/379557b9-ca93-499b-8511-3ab4d7edc31d" />
<img width="431" height="950" alt="Screenshot 2026-05-06 223141" src="https://github.com/user-attachments/assets/b7d1b2c1-e462-42b8-a4c9-31a1b18e3acb" />

