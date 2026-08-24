## Purpose

Allows auto-test host applications to replace the default splash loading animation area with a fully custom layout for brand-specific or feature-specific loading experiences.

## ADDED Requirements

### Requirement: Custom loading layout

The system SHALL provide a method that host applications can override to supply a custom layout resource ID for the splash screen's loading animation area.

- `getSplashLoadingLayoutResId()` SHALL return an integer layout resource ID
- When the method returns a non-zero value, the splash screen SHALL inflate the returned layout and display it in place of the default loading area (brand icon + progress bar + loading text)
- When the method returns 0 or is not overridden, the splash screen SHALL display the default loading area
- The method SHALL be declared in the `IAutoTestSplash` interface
- `AutoTestSplashActivity` SHALL implement the interface method with a default return value of 0
- The method SHALL be declared as `protected` (or `public` if R8 obfuscation requires it) to allow subclass override
- The custom layout SHALL have access to the same lifecycle context as the splash activity

#### Scenario: Host provides custom loading layout

- **WHEN** a host application overrides `getSplashLoadingLayoutResId()` to return a valid layout resource ID (e.g., `R.layout.custom_splash_loading`)
- **THEN** the splash screen SHALL inflate the custom layout and display it in place of the default loading animation area

#### Scenario: Host does not override custom loading layout

- **WHEN** a host application does not override `getSplashLoadingLayoutResId()` or returns 0
- **THEN** the splash screen SHALL display the default loading area (brand icon + progress bar + loading text) as before

#### Scenario: Custom layout is fully functional

- **WHEN** the splash screen inflates the custom layout
- **THEN** the custom layout's child views SHALL be measurable and visible within the splash screen
- **THEN** the splash screen SHALL proceed to finish and navigate to the target activity after preload completion, regardless of whether a custom or default layout is displayed