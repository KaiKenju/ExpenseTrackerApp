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
## OCR
.......
## Output
- Dark Mode 🌚

![tempsnip](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/4b287d36-caa5-4dd4-bd89-fec16f00dec3)
![tempsnip1](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/3c374101-718d-4a8d-9178-0c271e687d2c)
![tempsnip2](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/872b12e3-f7d4-433c-a2b4-e933f11f34b5)
![tempsnip3](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/ebe3fe18-d871-4fa5-91a6-2a870201e7eb)
- Light Mode 🌞

![l1](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/ddae8adc-79d6-4c15-93b0-5cb774116fcf)
![l2](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/5db618d9-8d41-44ed-a920-1030f4a5ba43)
![l3](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/53b5fefa-0569-4317-a4c1-efcbe3319dd3)
![l4](https://github.com/KaiKenju/ExpenseTrackerApp/assets/94727276/0fdbd074-c60c-464a-8d5b-d566dd756b55)
- We also support the multiple languages: English, Viet Nam, French