# Philippines High School Tambayan & AI Online Students Chat 🇵🇭

**Philippines High School Tambayan Chat** is a modern, student-safe online community app built for Filipino high schoolers. It features real-time barkada chatrooms, 8 interactive AI online classmates with realistic Tagalog/English responses, audio/video call simulation, campus FM background music, and DepEd-compliant safety guidelines.

---

## 🚀 Key Features

1. **8 Interactive AI Online Students**
   - **#100 Alex** – Mobile Legends / Gamer barkada
   - **#92 Bea** – Class Valedictorian / Honors student
   - **#36 Chris** – Funny joker / Meme master
   - **#50 Dianne** – Music lover / OPM enthusiast
   - **#48 Ethan** – Friendly / Student council vibe
   - **#79 Fiona** – Campus news & trending chismis
   - **#84 Gelo** – Varsity basketball / Athlete
   - **#66 Hannah** – Aesthetic & chill tambay

2. **Public Tambayan & 1-on-1 Private Messenger**
   - Public multi-channel chatrooms (General Lounge, High School Life, Hugot & Crushes, Study Hall, Gamers Den, OPM Jam).
   - Direct 1-on-1 private messaging with classmates.

3. **Audio & Video Call Simulation**
   - Dedicated Call Toolbar with quick-call triggers.
   - Interactive call dialog with live timer, mute mic toggle, camera switch toggle, and end call button.

4. **Document Actions & Campus FM**
   - Document toolbar for sharing and saving study materials and notes.
   - Built-in Campus FM background music player across rooms.

5. **Strict Admin / Owner Dashboard Isolation**
   - Isolated admin controls exclusively accessible by the authorized owner.
   - Zero access for regular student accounts or guest sessions.

6. **DepEd Safety & Child Protection Policy**
   - Aligned with DepEd Child Protection Policy (DO 40, s. 2012) and Anti-Bullying Act (RA 10627).

---

## 🛠️ Tech Stack & Architecture

- **Platform:** Android (API 26+) & Modern Web (PWA / Responsive HTML5)
- **Language:** Kotlin 2.x
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM + Clean Architecture + StateFlow
- **Backend / Services:** Firebase Firestore, Firebase Authentication, Cloud Sync
- **Build System:** Gradle (Kotlin DSL)

---

## 📦 How to Build APK for Hosting

### Option A: Via AI Studio UI (Fastest)
1. Go to the **top-right menu** or **Settings / Export** in Google AI Studio.
2. Select **"Push to GitHub"** to automatically create/sync your GitHub repository.
3. Select **"Export APK / Download APK"** to get your ready-to-install Android package.

### Option B: Building via Command Line / Android Studio
To compile the debug APK:
```bash
./gradlew assembleDebug
```
The generated APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

To compile a signed release APK:
```bash
./gradlew assembleRelease
```
The generated APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🌐 Hosting APK on GitHub Releases

1. In your GitHub repository, navigate to **Releases** > **Draft a new release**.
2. Tag your release (e.g., `v1.0.0`).
3. Drag and drop your built `app-debug.apk` or `app-release.apk` into the **Attach binaries** section.
4. Click **Publish release**. Students can download and install the APK directly from the release link!

---

## 📄 License & Compliance

Compliant with Republic Act No. 10627 (Anti-Bullying Act) and Republic Act No. 10173 (Data Privacy Act of 2012).
