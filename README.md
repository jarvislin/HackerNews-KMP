# Hacker News KMP

This project is designed to showcase the capabilities of **Kotlin Multiplatform Compose** by implementing both Android and iOS apps. The aim is to demonstrate how effectively this framework can create cross-platform applications and to learn new concepts that I hadn't encountered before.

![hn_16_9](https://github.com/jarvislin/HackerNews-KMP/assets/3839951/bc29705a-6e69-474c-8453-91485d99b458)


## Download


<a href="https://play.google.com/store/apps/details?id=com.jarvislin.hackernews"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/a6ed5faf-aaad-44a7-8910-fc0593343d6f" height=79/></a>
<a href="https://apps.apple.com/tw/app/hacker-news-reader-kmp/id6504872454"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/7631ba39-5713-40fb-a036-d866c939d993" height=80/></a>


## Tech Stack

1. Entire project written in [Kotlin](https://kotlinlang.org/)
2. UI developed with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/), following [Material 3](https://m3.material.io/) guidelines
3. Asynchronous tasks handled with [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
4. Dependency injection managed with [Koin](https://github.com/InsertKoinIO/koin)
5. API interactions handled by [Ktor Client](https://github.com/ktorio/ktor)
6. Time conversions using [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)
7. Serialization managed by [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization)

For the full list of dependencies used in the project, please check [this file](https://github.com/jarvislin/HackerNews-KMP/blob/main/gradle/libs.versions.toml).

## Architecture

Architecture follows **MVVM** and **Clean Architecture**.
