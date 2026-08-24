## Purpose

Defines how auto-test module's main activity layout handles system status bar insets, ensuring content is positioned below the status bar on devices with edge-to-edge rendering enabled.

## ADDED Requirements

### Requirement: Main layout respects system status bar insets

The main activity layout SHALL automatically adjust its top padding to accommodate the system status bar height, preventing content overlap with the status bar.

#### Scenario: Status bar does not overlap content
- **WHEN** the app launches on a device with edge-to-edge rendering (Android 15+)
- **THEN** the main layout content SHALL be positioned below the status bar, not obscured by it

#### Scenario: Backward compatibility on older devices
- **WHEN** the app launches on a device running Android 5.0-14
- **THEN** the layout behavior SHALL remain unchanged, with no visual regression