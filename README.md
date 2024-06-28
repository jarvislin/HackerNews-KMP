# Hacker News KMP

This is a project designed to showcase Kotlin Multiplatform Compose's capabilities, which is targeting to Android and iOS.

![screenshots](https://github.com/jarvislin/HackerNews-KMP/assets/3839951/fb88b1fc-a0ca-484d-807b-52aceaf26760)


## Download

<a href="https://play.google.com/store/apps/details?id=com.jarvislin.hackernews"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/a0d1ea83-3e58-449e-9055-2a39449aee52" height=79/></a>
<a href="https://apps.apple.com/tw/app/hacker-news-reader-kmp/id6504872454"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/7631ba39-5713-40fb-a036-d866c939d993" height=80/></a>



## Tech Stack
1. Material 3
2. Koin
3. voyager
4. napier
5. ktor
6. richeditor
7. compose-webview-multiplatform

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
