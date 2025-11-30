# ☆ Math.IO Mobile Quiz Codebase ☆

> "It's just a cup of coffee, baby~"

Welcome to the internal source directory for **Math.IO**! This repository contains the complete source code for a robust Android mobile quiz application developed for the SICI 4185 course (Introducción a la Programación de Dispositivos Móviles).

The application features a multi-activity menu, dynamic question generation, a 5-life health system, and local high score persistence. These are the files that make the magic happen!

## ☆ What's Inside?

Here is a breakdown of the core files and folders you'll find in the `app/src/main/` directory:

| File/Folder | Purpose | Key Features |
| :--- | :--- | :--- |
| **`java/`** | **The Brains.** Contains all primary Java logic files. | Implements game loop, health system, persistence, and navigation. |
| &nbsp;&nbsp;&nbsp;`MainActivity.java` | **Core Game Logic.** Controls the game screen, timer (60s), scoring, health, sound (`SoundPool`), and high score saving (`SharedPreferences`). | `generateQuestion()`, `chooseAnswer()`, `resumeTimer()`, `onSaveInstanceState()`. |
| &nbsp;&nbsp;&nbsp;`MenuActivity.java` | **Launcher/Menu Logic.** Handles category/difficulty selection and game initiation. | `selectCategory()`, `startGame()`, `openAbout()`. |
| &nbsp;&nbsp;&nbsp;`AboutActivity.java` | **Developer Info.** Handles the simple About screen and external link navigation (Ko-fi, GitHub). | `goToKofi()`, `goToGithub()`. |
| **`res/`** | **The Assets.** Contains all resources referenced by the code and layouts. | Divided into `layout/`, `values/`, `drawable/`, and `raw/`. |
| &nbsp;&nbsp;&nbsp;`layout/` | Contains all UI structure files (`.xml`). | Defines `activity_menu.xml` and `activity_main.xml` (includes 10% buffer for visual balance). |
| &nbsp;&nbsp;&nbsp;`raw/` | **Sound Resources.** Holds short audio files for game feedback. | `.wav` or `.mp3` files for correct/wrong/new record sounds. |
| &nbsp;&nbsp;&nbsp;`AndroidManifest.xml` | **Configuration File.** Registers all three activities and sets `MenuActivity` as the LAUNCHER. | Defines app theme, icons, and navigation structure. |

---

## ☆ Requirements

To compile and run this mobile application, ensure your development environment is set up with:

* **Android Studio**
* **Java Development Kit (JDK)**
* **Android SDK** (Modern API level)
* **AndroidX CardView Dependency** (Required for the `AboutActivity` layout).

## ☆ Coding Style & Attribution

All C# and Java code across my projects follows a specific stylish header and clean structure. Please keep these headers intact if you modify or redistribute the source files.

Here is an example of the file header format:

```java
/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Java
☆ File Name: Example.java
☆ Date: 2025-11-29
☆
*/

```
---

## ☆ License

This code is licensed under the **MIT License**!

A credit to **MelodyHSong** is always appreciated.

---
*Kotlin, I hate that. - MelodyHSong*
