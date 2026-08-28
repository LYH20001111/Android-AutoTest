# Splash Screen Capability Delta — 启动无缝衔接与等待模型

## ADDED Requirements

### Requirement: Seamless launch without blank frames

The system SHALL eliminate blank or white flash frames during the host application's cold start by ensuring the system starting window, the splash content layout, and the post-splash theme use continuous backgrounds.

- The host app SHALL apply `Theme.AutoTest.SplashScreen` to its launcher (splash) activity in `AndroidManifest.xml` so the system starting window renders the splash background instead of a blank window
- `AutoTestSplashActivity` SHALL call `SplashScreen.installSplashScreen(this)` before `super.onCreate()` while the splash theme is active, and SHALL NOT call `setTheme()` afterwards to switch themes
- `windowSplashScreenBackground`, the splash root layout background, and the default loading layout background SHALL all resolve to the same color resource `auto_test_splash_background`
- The `postSplashScreenTheme` SHALL be `Theme.AutoTest.Main`, which declares a `windowBackground` matching `auto_test_splash_background`, so no white frame appears between splash dismissal and the main activity's first draw

#### Scenario: Cold start shows splash background continuously

- **WHEN** the host app is cold-started and the launcher activity declares `Theme.AutoTest.SplashScreen`
- **THEN** the system starting window SHALL display the splash background color
- **AND** the splash content layout SHALL render with the identical background color, with no visible color jump or blank frame between them

#### Scenario: Host forgets to declare the splash theme

- **WHEN** the host app's launcher activity does not declare `Theme.AutoTest.SplashScreen` in the manifest
- **THEN** the library SHALL still function (splash content renders after `onCreate`)
- **AND** the integration documentation SHALL state that the manifest theme declaration is required to avoid a blank starting window

#### Scenario: Transition to main activity has no white flash

- **WHEN** the splash screen is dismissed and the target activity starts
- **THEN** the window background SHALL remain `auto_test_splash_background` until the target activity draws its own content

### Requirement: Handler-based preload wait model

The system SHALL schedule the navigation to the target activity using main-thread handler callbacks instead of a busy-wait polling thread.

- The preload work (`onPreloadData()`) SHALL run on a background thread
- When preload finishes, the background thread SHALL post a single callback to the main thread, which SHALL compute the remaining minimum display duration as `max(0, getMinDisplayDuration() - elapsed)` and schedule `navigateToTarget` via `Handler.postDelayed`
- The system SHALL NOT spawn a dedicated thread that polls preload state with `Thread.sleep`
- If the splash activity is finishing or destroyed before the scheduled navigation fires, the navigation SHALL be skipped
- All pending handler callbacks SHALL be removed in `onDestroy()`

#### Scenario: Preload finishes before minimum display duration

- **WHEN** preload completes and the elapsed time since splash start is less than `getMinDisplayDuration()`
- **THEN** the system SHALL wait exactly for the remaining duration on the main handler before navigating
- **AND** no polling thread SHALL be active during the wait

#### Scenario: Preload finishes after minimum display duration

- **WHEN** preload completes and the elapsed time already exceeds `getMinDisplayDuration()`
- **THEN** the system SHALL navigate to the target activity immediately (zero delay)

#### Scenario: Splash activity destroyed before navigation

- **WHEN** the splash activity is destroyed (e.g., user presses Back) before the scheduled navigation fires
- **THEN** the pending callback SHALL be cancelled in `onDestroy()`
- **AND** no navigation SHALL be attempted against the destroyed activity
