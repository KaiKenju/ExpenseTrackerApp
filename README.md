# 🚀 ExpenseTrackerApp (12/2023)
Tracker Money app helps you track and manage your financial transactions on the Android platform. 
This application is built in Java in the Android Studio development environment.

# 🚀 Requirement
- Android Studio: Android studio giraffe 2022.3.1 patch 3 or more
- Android SDK:  Android Gradle Plugin Version 7.2.1 or more and 
                Gradle Version : 7.3.3 or more

# 🚀 Dependencies
```
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-analytics")
implementation 'com.firebaseui:firebase-ui-database:7.2.0'
implementation 'com.google.firebase:firebase-auth' // Or the latest version
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'com.github.bumptech.glide:glide:4.16.0'
implementation 'com.google.android.gms:play-services-auth:20.7.0'
```
## Libraries & Techonologies
- **RecyclerView:** To display a list of transactions.
- **MPAndroidChart:**  Draw charts and graphs.
- **Glide**: to load and display images on Android. It helps optimize downloading and caching images from the internet or from local storage.
- **Firebase UI Database**: provides out-of-the-box user interfaces to interact with the Firebase Realtime Database. Build user interfaces easily and quickly to display and manage data from Firebase Database.
- **Firebase Authentication**: authenticate users in your app through Firebase. It supports authentication methods such as email/password, phone number authentication, Google, Facebook authentication, etc.
- **Google Play Services Authentication**: User authentication through Google Play services, including authentication through your app's Google account.

## MVP Model
![image](https://github.com/KaiKenju/GithubBrowser/assets/94727276/0e03853e-e3ac-4c2d-96d3-4562630e347f)

MVP consists of three classes Model, View, and Presenter.

In MVP, 
- View and Model have a clearer separation.

- MVP, View is responsible for delegating input to Presenter.

- MVP, Presenter and View should have a 1-1 relationship, with each Presenter having a link to its View through the interface.

- MVP, View are linked to the Model directly through data bindings.

- Unit testing is easier, as the View knows the Presenter through an interface that can easily be mocked.
## Output
- Dark Mode 🌚
![416792017_276722761759641_5197669841158283943_n](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/18b71710-ecfb-484b-82cb-05f812bc10a4) ![Capture2](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/336d5bfe-b74c-47c2-ada8-137694de2205) ![Capture3](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/a596f2f7-36c0-40be-a4f7-06ebf5e88921) ![Capture4](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/e2d60a1f-90e0-4a88-be80-e950f112fa24)
- Light Mode 🌞