# Voice Notes App

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg" alt="UI">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

A minimalist voice notes application for Android with speech-to-text functionality, inspired by Nothing's design philosophy.

[🇷🇺 Русская версия](README_RU.md)

## ✨ Features

- 🎤 **Voice Recording** - Quick voice recording with a single tap
- 🗣️ **Speech-to-Text** - Automatic conversion of speech to text using Android's built-in speech recognition
- 📝 **Notes List** - View all your voice notes in a clean, organized list
- ✅ **Complete Notes** - Mark notes as complete with a smooth fade-out animation
- 🎨 **Nothing-Inspired Design** - Minimalist black background with red accents
- 🔴 **Pulsing Record Button** - Animated microphone button indicates active recording
- 🇷🇺 **Russian Language Support** - Native support for Russian speech recognition
- 📱 **Home Screen Widget** - Quick access widget for instant note recording
- 💾 **Local Storage** - All notes stored locally using Room database

## 🎨 Design Philosophy

The app follows Nothing's minimalist design language:
- **Colors**: Deep black background (#0A0A0A), dark grays (#1A1A1A, #2A2A2A), vibrant red accent (#FF0000)
- **Typography**: Light, modern Roboto font
- **Animations**: Smooth, purposeful transitions
- **Interface**: Clean, distraction-free experience

## 📥 Download & Installation

### Option 1: Download from GitHub Actions

1. Go to the [Actions tab](../../actions/workflows/build-apk.yml)
2. Click on the latest successful workflow run
3. Download the `app-debug` artifact
4. Extract the ZIP file to get `app-debug.apk`
5. Transfer the APK to your Android device
6. Enable "Install from Unknown Sources" in your device settings
7. Install the APK

### Option 2: Build from Source

**On Linux/Mac:**
```bash
# Clone the repository
git clone https://github.com/avtoZAZ/voice-notes-app.git
cd voice-notes-app

# Build the APK
./gradlew assembleDebug

# The APK will be available at:
# app/build/outputs/apk/debug/app-debug.apk
```

**On Windows:**
```cmd
REM Clone the repository
git clone https://github.com/avtoZAZ/voice-notes-app.git
cd voice-notes-app

REM Build the APK
gradlew.bat assembleDebug

REM The APK will be available at:
REM app\build\outputs\apk\debug\app-debug.apk
```

## 🛠️ Development

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34
- Gradle 8.2+
- Internet access to Google Maven Repository (dl.google.com)

**Note**: The project requires access to Google's Maven repository to download the Android Gradle Plugin and dependencies. If you're in a restricted network environment, the GitHub Actions workflow will handle the build automatically when you push to the repository.

### Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room
- **Async**: Kotlin Coroutines & Flow
- **Speech Recognition**: Android SpeechRecognizer API

### Project Structure

```
app/src/main/java/com/avtozaz/voicenotes/
├── MainActivity.kt              # Main activity with speech recognition
├── ui/
│   ├── MainScreen.kt           # Compose UI screens
│   └── theme/
│       ├── Color.kt            # App colors
│       └── Theme.kt            # Material3 theme
├── data/
│   ├── VoiceNote.kt           # Data entity
│   ├── NoteDao.kt             # Room DAO
│   └── NoteDatabase.kt        # Room database
├── viewmodel/
│   └── NoteViewModel.kt       # ViewModel for notes
└── widget/
    └── VoiceNoteWidget.kt     # Home screen widget
```

### Building the Project

**On Linux/Mac:**
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

**On Windows:**
```cmd
REM Debug build
gradlew.bat assembleDebug

REM Release build
gradlew.bat assembleRelease

REM Run tests
gradlew.bat test

REM Install on connected device
gradlew.bat installDebug
```

## 🚀 Usage

1. **Recording a Note**:
   - Tap the red microphone button
   - Grant microphone permission if prompted
   - Speak your note
   - The app will automatically convert your speech to text

2. **Completing a Note**:
   - Tap the checkmark button on any note
   - The note will fade out and be removed

3. **Using the Widget**:
   - Long-press on your home screen
   - Add the "Voice Note Widget"
   - Tap the widget to quickly open the app

## 📱 Screenshots

_Screenshots coming soon_

## 🔒 Permissions

- **RECORD_AUDIO**: Required for voice recording and speech recognition
- **INTERNET**: Required for speech recognition service (Google's cloud service)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Design inspiration from Nothing Phone
- Built with Jetpack Compose
- Uses Android's native speech recognition

---

Made with ❤️ for minimalist design lovers