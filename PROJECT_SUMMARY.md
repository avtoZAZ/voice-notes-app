# Voice Notes Android App - Project Summary

## Overview
This is a complete Android application for voice notes with speech-to-text functionality, designed in the minimalist Nothing phone style.

## Key Features Implemented

### Core Functionality
1. **Voice Recording with Dual Modes**:
   - **Single Tap Mode**: Records until silence detected (Voice Activity Detection)
   - **Long Press Mode**: Records while button is held
2. **Audio Recording**: MediaRecorder API saves .3gp audio files to internal storage
3. **Speech-to-Text**: Automatic conversion using Android's SpeechRecognizer API
4. **Notes List**: View all notes in a scrollable list with timestamps
5. **Complete Notes**: Mark notes as done with smooth fade-out animation
6. **Home Screen Widget**: Circular red button widget for instant recording
   - Records without opening app
   - Uses Voice Activity Detection for auto-stop
7. **Background Recording**: Foreground service with notification during recording

### Design (Nothing Style)
- Black background (#0A0A0A)
- Dark gray cards (#1A1A1A, #2A2A2A)  
- Red accent (#FF0000) for interactive elements
- Pulsing record button animation during recording
- Circular widget button (80dp) with white mic icon
- Smooth fade animations for note completion
- Minimalist Roboto font

### Technical Implementation

#### Architecture: MVVM
- **Model**: VoiceNote data class with Room database
- **View**: Jetpack Compose UI (MainScreen.kt)
- **ViewModel**: NoteViewModel managing state and database operations
- **Service**: VoiceRecordingService for background recording

#### Data Layer (Room Database)
- **VoiceNote Entity**: id, text, timestamp, isCompleted, audioFilePath
- **NoteDao**: CRUD operations with Flow for reactive updates
- **NoteDatabase**: Singleton Room database instance (version 2)
- **Migration**: Destructive migration for development simplicity

#### UI Layer (Jetpack Compose)
- **MainScreen**: Main UI with notes list and FAB
- **NoteCard**: Individual note item with text and complete button
- **RecordButton**: Animated FAB with gesture detection for tap/long-press
- **Theme**: Material3 dark theme with Nothing colors

#### Recording Service (VoiceRecordingService)
- **Foreground Service**: Runs in background with notification
- **MediaRecorder**: Records audio to .3gp files
- **SpeechRecognizer**: Converts speech to text
- **Voice Activity Detection**: Monitors amplitude to detect silence
  - Threshold: 500 (amplitude)
  - Duration: 15 checks (~1.5 seconds of silence)
- **Two Modes**:
  - MODE_VAD (0): Auto-stops on silence detection
  - MODE_HOLD (1): Stops when release signal received

#### Widget (VoiceNoteWidget)
- **AppWidgetProvider**: Handles widget lifecycle
- **Circular Button**: 80dp red circle with white mic icon
- **PendingIntent**: Broadcasts recording action
- **No App Launch**: Records directly from home screen
- **Auto-stop**: Uses VAD mode for automatic termination

## Project Structure

```
voice-notes-app/
├── .github/workflows/
│   └── build-apk.yml          # CI/CD for APK builds
├── app/
│   ├── build.gradle.kts       # App-level Gradle config
│   ├── proguard-rules.pro     # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/avtozaz/voicenotes/
│       │   ├── MainActivity.kt         # Main activity with recording modes
│       │   ├── data/
│       │   │   ├── VoiceNote.kt       # Data entity with audioFilePath
│       │   │   ├── NoteDao.kt         # Room DAO
│       │   │   └── NoteDatabase.kt    # Room database (v2)
│       │   ├── service/
│       │   │   └── VoiceRecordingService.kt  # Foreground recording service
│       │   ├── ui/
│       │   │   ├── MainScreen.kt      # Compose UI with gesture detection
│       │   │   └── theme/
│       │   │       ├── Color.kt       # App colors
│       │   │       └── Theme.kt       # Material3 theme
│       │   ├── viewmodel/
│       │   │   └── NoteViewModel.kt   # ViewModel
│       │   └── widget/
│       │       └── VoiceNoteWidget.kt # Widget with recording action
│       └── res/
│           ├── drawable/              # Icon assets (mic icon, widget bg)
│           ├── layout/                # Widget layout
│           ├── mipmap-*/              # Launcher icons (all densities)
│           ├── values/                # Strings, colors, themes
│           └── xml/                   # Widget configuration
├── gradle/wrapper/                    # Gradle wrapper files
├── build.gradle.kts                   # Root Gradle config
├── settings.gradle.kts                # Gradle settings
├── gradle.properties                  # Gradle properties
├── gradlew                            # Unix Gradle wrapper script
├── gradlew.bat                        # Windows Gradle wrapper script
├── .gitignore                         # Git ignore rules
├── README.md                          # English documentation
└── README_RU.md                       # Russian documentation
```

## Dependencies

### Jetpack Compose
- compose-bom:2023.10.01
- Material3
- Material Icons Extended

### Architecture Components
- ViewModel (lifecycle-viewmodel-compose:2.6.2)
- Lifecycle Runtime (lifecycle-runtime-compose:2.6.2)

### Database
- Room (room-runtime, room-ktx:2.6.0)
- Room Compiler (kapt)

### Kotlin
- Kotlin Coroutines (kotlinx-coroutines-android:1.7.3)
- Kotlin Stdlib

### Build Tools
- Android Gradle Plugin: 7.4.2
- Kotlin Gradle Plugin: 1.8.20
- Gradle: 8.2

## Build Configuration

### Minimum Requirements
- minSdk: 24 (Android 7.0)
- targetSdk: 34 (Android 14)
- compileSdk: 34

### Build Variants
- Debug: app-debug.apk (with debugging symbols)
- Release: app-release.apk (optimized, needs signing)

### Permissions Required
- RECORD_AUDIO: For voice recording
- INTERNET: For speech recognition service
- FOREGROUND_SERVICE: For background recording service
- POST_NOTIFICATIONS: For recording notification (Android 13+)

## GitHub Actions Workflow

The `.github/workflows/build-apk.yml` workflow:
1. Checks out the repository
2. Sets up JDK 17
3. Grants gradlew execute permission
4. Builds debug APK using `./gradlew assembleDebug`
5. Uploads APK as artifact

Triggered on:
- Push to main/master branch
- Pull requests to main/master branch

## Usage Instructions

### For Users
1. Download APK from GitHub Actions artifacts
2. Install on Android device (enable "Unknown Sources")
3. Grant microphone permission when prompted
4. **In-App Recording**:
   - Single tap: Records until silence detected
   - Long press: Records while button is held
5. **Widget Recording**:
   - Add widget to home screen
   - Tap widget button to record (auto-stops on silence)
6. Tap checkmark to complete/delete note

### For Developers
1. Clone repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device
5. Build APK: `./gradlew assembleDebug`

## Limitations & Notes

1. **Network Access**: Build requires access to Google's Maven repository (dl.google.com)
2. **Language**: Currently configured for Russian ("ru-RU"), can be changed in VoiceRecordingService
3. **Speech Recognition**: Requires Google services on device
4. **Storage**: All data and audio files stored locally in app's internal storage
5. **Widget**: Records directly without opening app, uses VAD for auto-stop
6. **VAD Sensitivity**: Silence threshold and duration can be tuned in VoiceRecordingService
7. **Audio Format**: Saves as .3gp files (AMR_NB codec) for small file size

## Future Enhancements (Not Implemented)

Potential improvements:
- Multiple language support / language selection
- Audio playback from saved files
- Export notes to file
- Cloud sync
- Note editing
- Voice playback
- Note categories/tags
- Search functionality
- Dark/light theme toggle
- Note sharing

## Testing Notes

The project was created with:
- ✅ Complete source code structure
- ✅ All necessary configuration files
- ✅ Resource files and assets
- ✅ GitHub Actions workflow
- ✅ Comprehensive documentation
- ⚠️ Local build not verified (network restrictions)
- ⏳ GitHub Actions build pending (will verify in CI)

## License
MIT License (as documented in README.md)

## Author
Created for avtoZAZ
