# SoundEventLib

A lightweight, lifecycle-aware Android library for playing sounds in response to named application events.

---

## Features

- Named events (`SUCCESS`, `ERROR`, `WARNING`, `SCAN_DETECTED`, or custom)
- Simple builder-based configuration
- Global enable/disable toggle
- Volume control at runtime
- Two playback modes: `OVERLAP` (SoundPool streams) and `QUEUE` (sequential)
- No UI framework dependencies (works with Compose, XML, or headless)
- Zero heavyweight dependencies — only `core-ktx`

---

## Installation

### Option A — JitPack (recommended)

No credentials required. JitPack builds directly from GitHub.

**Step 1.** Add JitPack to your project's `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2.** Add the dependency in your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.GuitHub75:sound-event-lib:v1.1.0")
}
```

That's it — no tokens, no credentials.

### Option B — AAR file (offline/manual)

Build the AAR:
```bash
./gradlew :soundeventlib:assembleRelease
```
Output: `soundeventlib/build/outputs/aar/soundeventlib-release.aar`

Copy it to your project's `libs/` folder and add:
```kotlin
dependencies {
    implementation(files("libs/soundeventlib-release.aar"))
}
```

### Option C — Local module

In `settings.gradle.kts`:
```kotlin
include(":soundeventlib")
```

In your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":soundeventlib"))
}
```

---

## Integration

### 1. Initialize in Application

The library includes default sounds for all standard events. No sound files needed in your project.

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundManager.init(
            context = this,
            soundConfig = SoundConfig.Builder()
                .volume(0.9f)
                .playbackMode(PlaybackMode.OVERLAP)
                .build()
        )
    }

    override fun onTerminate() {
        SoundManager.release()
        super.onTerminate()
    }
}
```

Register your Application class in `AndroidManifest.xml`:
```xml
<application android:name=".MyApp" ...>
```

### 2. Play sounds anywhere

```kotlin
SoundManager.play(SoundEvent.SUCCESS)
SoundManager.play(SoundEvent.ERROR)
SoundManager.play(SoundEvent.WARNING)
SoundManager.play(SoundEvent.SCAN_DETECTED)
SoundManager.play(SoundEvent.NOTIFICATION)
```

### 3. Override default sounds (optional)

Use `addEvent()` to replace any default sound with your own:
```kotlin
SoundConfig.Builder()
    .addEvent(SoundEvent.SUCCESS, R.raw.my_success)
    .addEvent(SoundEvent.ERROR,   R.raw.my_error)
    .build()
```

### 4. Custom events

```kotlin
val BARCODE_SCAN = SoundEvent.custom("BARCODE_SCAN")
SoundManager.register(BARCODE_SCAN, R.raw.beep)
SoundManager.play(BARCODE_SCAN)
```

### 5. Runtime controls

```kotlin
SoundManager.setEnabled(false)   // mute all sounds
SoundManager.setEnabled(true)    // unmute
SoundManager.setVolume(0.5f)     // 50% volume (0.0 – 1.0)
```

---

## API Reference

| Method                                   | Description |
|------------------------------------------|-------------|
| `SoundManager.init(context, config)`     | Initialize with a `SoundConfig`. Call once in Application. |
| `SoundManager.play(event)`               | Play the sound mapped to `event`. No-op if disabled or unregistered. |
| `SoundManager.register(event, rawResId)` | Add or update an event mapping at runtime. |
| `SoundManager.setEnabled(Boolean)`       | Globally enable/disable playback. |
| `SoundManager.setVolume(Float)`          | Adjust volume (0.0–1.0). |
| `SoundManager.release()`                 | Free all resources. Call when no longer needed. |
| `SoundManager.isInitialized`             | Whether `init()` has been called. |

---

## Architecture

```
SoundManager          ← Public API (object/singleton)
├── SoundConfig       ← Immutable configuration (Builder pattern)
├── EventRegistry     ← Maps SoundEvent → loaded sound ID
├── SoundPlayer       ← Interface abstracting audio engine
│   └── SoundPoolPlayer  ← SoundPool implementation
└── PlaybackStrategy  ← Strategy pattern for playback behaviour
    ├── OverlapStrategy  ← Simultaneous streams
    └── QueueStrategy    ← Sequential queue
```

### Design decisions

- **SoundPool over MediaPlayer** for short notification sounds: lower latency, native multi-stream support, smaller footprint.
- **Strategy pattern** (`PlaybackStrategy`) makes adding new playback behaviours (e.g. priority queue, deduplication) a drop-in change with zero impact on callers.
- **EventRegistry** is a pure data structure — no Android dependencies, trivially testable.
- **`internal` visibility** on implementation classes keeps the public surface minimal and stable.

---

## Playback Modes

| Mode | Behaviour |
|------|-----------|
| `OVERLAP` | Each `play()` call starts a new SoundPool stream immediately, overlapping existing ones. |
| `QUEUE` | Sounds are enqueued and played one after another. |

---

## Running Tests

```bash
./gradlew :soundeventlib:test
```

---

## Requirements

- Min SDK 23 (Android 6.0)
- Kotlin 1.9+
- No additional permissions required
