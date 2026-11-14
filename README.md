# KSUN ZL65 AutoSpeaker

Automatic speakerphone for incoming and outgoing GSM calls on the KSUN ZL65 Android POC.

## Verified on hardware

- Device: KSUN ZL65 Android POC
- Firmware reports Android 9 / API 28
- Package: `hu.videotour.ksunautospeaker`
- App version: 1.1 (`versionCode 2`)
- Build environment: AndroidIDE
- Android Gradle Plugin: 4.0.1
- Gradle Wrapper: 6.1.1

The maintainer verified that the foreground call-monitor service starts automatically after reboot without opening the app, and that both outgoing and incoming GSM calls are automatically routed to speakerphone.

## Features

- Requests the Phone permission on first launch.
- Starts a foreground call-monitor service after permission is granted.
- Registers a boot receiver for `BOOT_COMPLETED`.
- Monitors `TelephonyManager.CALL_STATE_OFFHOOK`.
- Retries speakerphone routing because Telecom may override the initial request.
- Leaves wired-headset and Bluetooth audio routes unchanged.

## Installation

1. Build the APK from this source, or install an APK attached to a GitHub Release when available.
2. Open KSUN AutoSpeaker once after installation.
3. Grant the Phone permission.
4. Leave the app installed. The service starts automatically after subsequent device restarts.

## Build with AndroidIDE

Open the project root (the folder containing `settings.gradle`) in AndroidIDE and build the debug APK. The project includes the Gradle wrapper JAR and uses Gradle 6.1.1 with Android Gradle Plugin 4.0.1.

## Project structure

- `app/src/main/AndroidManifest.xml` — permissions and component declarations
- `MainActivity.java` — first-run permission request and service startup
- `CallMonitorService.java` — foreground service and call-state/audio-route handling
- `BootReceiver.java` — automatic service startup after boot

## Compatibility

Verified on the KSUN ZL65 firmware configuration above. Android audio routing is vendor- and firmware-dependent; operation on other phones or POC devices is not guaranteed. A wired headset or Bluetooth route is intentionally left in control.

This is an independent community project and is not affiliated with KSUN or MediaTek.

## License

MIT. See [LICENSE](LICENSE).
