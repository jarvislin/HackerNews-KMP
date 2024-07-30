![Static Badge](https://img.shields.io/badge/Platform-iOS-blue?style=flat)
![Static Badge](https://img.shields.io/badge/Platform-Android-green?style=flat)
![GitHub License](https://img.shields.io/github/license/jarvislin/HackerNews-KMP?style=flat)


# Hacker News KMP
![hn_16_9](https://github.com/jarvislin/HackerNews-KMP/assets/3839951/bc29705a-6e69-474c-8453-91485d99b458)

This project is designed to showcase the capabilities of **Kotlin Multiplatform Compose** by implementing both Android and iOS apps. The aim is to demonstrate how effectively this framework can create cross-platform applications and learn new concepts I hadn't encountered before.


## Download


<a href="https://play.google.com/store/apps/details?id=com.jarvislin.hackernews"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/a6ed5faf-aaad-44a7-8910-fc0593343d6f" height=79/></a>
<a href="https://apps.apple.com/tw/app/hacker-news-reader-kmp/id6504872454"><img src="https://github.com/jarvislin/HackerNews-KMP/assets/3839951/7631ba39-5713-40fb-a036-d866c939d993" height=80/></a>

## Article

[How to Develop and Publish an App on Two Platforms Within a Week?](https://medium.com/p/918cea37dda2)


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

Architecture follows **[MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)** and **[Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)**.

![clean_architecture_mvvm](https://github.com/jarvislin/HackerNews-KMP/assets/3839951/a3823b81-1e99-4457-bf7c-fcbe5051ed34)

## License

[Mozilla Public License Version 2.0](https://github.com/jarvislin/HackerNews-KMP/blob/main/LICENSE)
