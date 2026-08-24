## Purpose

Defines how the splash screen title behaves when the host app sets a long title string, ensuring the title display is visually clean regardless of title length.

## ADDED Requirements

### Requirement: Long title display handling

The system SHALL display the splash screen title text without visual degradation (unexpected line wrapping) when the host app sets a title longer than the available display width at the default font size.

#### Scenario: Short title fits in one line
- **WHEN** the host app sets a title whose rendered width at 50sp fits within the screen width
- **THEN** the title SHALL be displayed in a single line at the full 50sp font size

#### Scenario: Long title auto-shrinks to fit
- **WHEN** the host app sets a title whose rendered width at 50sp exceeds the screen width
- **THEN** the system SHALL automatically reduce the font size so that the title fits in a single line without wrapping

#### Scenario: Minimum readable font size
- **WHEN** the title text is extremely long and auto-shrinking would reduce the font size below a readable minimum
- **THEN** the system SHALL truncate the title with an ellipsis at the end, preserving single-line display

#### Scenario: Backward compatibility
- **WHEN** the host app sets a short title (e.g., the default value from `R.string.auto_test`)
- **THEN** the display behavior SHALL be visually identical to the current behavior (single line, 50sp, centered)