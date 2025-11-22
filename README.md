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

- 🎤 **Voice Recording with Two Modes**:
  - **Single Tap**: Records until silence is detected (Voice Activity Detection)
  - **Long Press**: Records while button is held, stops when released
- 🎙️ **MediaRecorder Integration** - High-quality audio recording saved to device
- 🗣️ **Speech-to-Text** - Automatic conversion of speech to text using Android's built-in speech recognition
- 📝 **Notes List** - View all your voice notes in a clean, organized list
- ✅ **Complete Notes** - Mark notes as complete with a smooth fade-out animation
- 🎨 **Nothing-Inspired Design** - Minimalist black background with red accents
- 🔴 **Pulsing Record Button** - Animated microphone button indicates active recording
- 🇷🇺 **Russian Language Support** - Native support for Russian speech recognition
- 📱 **Home Screen Widget** - Circular red button widget for instant recording from home screen
  - Tap widget to start recording
  - Automatically stops when silence is detected
  - Records without opening the app
- 💾 **Local Storage** - All notes and audio files stored locally using Room database
- 🔔 **Foreground Service** - Reliable recording with notification during capture

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
- **Audio Recording**: Android MediaRecorder API
- **Background Processing**: Foreground Service

### Project Structure

```
app/src/main/java/com/avtozaz/voicenotes/
├── MainActivity.kt              # Main activity with recording coordination
├── ui/
│   ├── MainScreen.kt           # Compose UI with dual recording modes
│   └── theme/
│       ├── Color.kt            # App colors (Nothing style)
│       └── Theme.kt            # Material3 theme
├── data/
│   ├── VoiceNote.kt           # Data entity with audio file path
│   ├── NoteDao.kt             # Room DAO
│   └── NoteDatabase.kt        # Room database
├── viewmodel/
│   └── NoteViewModel.kt       # ViewModel for notes
├── service/
│   └── VoiceRecordingService.kt  # Foreground service for recording
└── widget/
    └── VoiceNoteWidget.kt     # Home screen widget with recording
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

### In-App Recording

1. **Single Tap Recording** (Voice Activity Detection):
   - Tap the red microphone button once
   - Grant microphone permission if prompted
   - Speak your note
   - Recording automatically stops when silence is detected (~1.5 seconds)
   - Speech is converted to text and saved

2. **Long Press Recording** (Hold Mode):
   - Long-press the red microphone button
   - Keep holding while speaking
   - Release the button when done
   - Speech is converted to text and saved

### Widget Recording

1. **Adding the Widget**:
   - Long-press on your home screen
   - Tap "Widgets"
   - Find and add the "Voice Note Widget"
   - You'll see a circular red button with a white microphone icon

2. **Recording with Widget**:
   - Tap the widget button on your home screen
   - Grant microphone permission if prompted
   - Speak your note (no need to open the app)
   - Recording automatically stops when silence is detected
   - Note is saved and appears in the app

### Managing Notes

1. **Viewing Notes**:
   - Open the app to see all your saved notes
   - Each note shows the transcribed text and timestamp

2. **Completing Notes**:
   - Tap the checkmark button on any note
   - The note will fade out and be removed

## 📱 Screenshots

_Screenshots coming soon_

## 🔒 Permissions

- **RECORD_AUDIO**: Required for voice recording and speech recognition
- **INTERNET**: Required for speech recognition service (Google's cloud service)
- **FOREGROUND_SERVICE**: Required for background recording from widget
- **POST_NOTIFICATIONS**: Required for showing recording notification (Android 13+)

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