# Runtime Performance Capability Specification

## Purpose

Defines the performance and resource-hygiene requirements of the auto-test module: configuration caching, annotation reflection caching, batch database loading, off-main-thread startup IO, static reference cleanup, and list binding reuse.

## ADDED Requirements

### Requirement: Configuration properties caching

The system SHALL load and parse `config.properties` at most once per process.

- `ReflectionUtils.getConfig(key)` SHALL return values from a cached `Properties` instance after the first successful load
- Subsequent calls SHALL NOT reopen or reparse the properties file
- If the properties file cannot be loaded, the behavior SHALL remain a `RuntimeException` as before

#### Scenario: Repeated config reads

- **WHEN** `getConfig` is invoked multiple times within one process
- **THEN** the properties file SHALL be read from disk only on the first invocation
- **AND** all invocations SHALL return the same values as the uncached implementation

### Requirement: Annotation value caching

The system SHALL provide a cached annotation value lookup whose results are identical to reflection-based lookup, and hot paths SHALL use it.

- The cached lookup SHALL key by annotated element and member name and store resolved values in a thread-safe cache
- `ExecutionFragment` SHALL resolve the test item name through the cached lookup instead of invoking uncached reflection repeatedly for the same element and member
- `BaseTestCase` SHALL resolve each `TestCase` annotation member (name, enDes, tip) at most once per case execution and reuse the resolved values

#### Scenario: Same value as uncached lookup

- **WHEN** the cached lookup is invoked for an element and member that the uncached lookup can resolve
- **THEN** it SHALL return a value equal to the uncached lookup result

#### Scenario: Repeated access is served from cache

- **WHEN** the same element and member are requested multiple times
- **THEN** reflection SHALL be performed only on the first request
- **AND** subsequent requests SHALL return the cached value

### Requirement: Batch test history loading

The system SHALL load test execution history with a single bulk data query instead of one query per test item.

- `ResultDao` SHALL expose a query returning all `ResultDataEntity` rows
- `AutoTestMainActivity` startup loading SHALL query all result items once and all result data once, then group the data rows by `className` in memory
- The loaded `ResultItem` list content and order SHALL match the previous per-item loading behavior

#### Scenario: History with multiple test items

- **WHEN** the database contains N result items with associated data rows
- **THEN** startup loading SHALL issue exactly 2 queries (items + data) regardless of N
- **AND** every `ResultItem` SHALL contain the same data rows as before

### Requirement: No private-field reflection for start-time flag

The system SHALL restore the `isStartTimeSet` flag of a `ResultItem` from persistence through a public API instead of reflecting on the private field.

- `ResultItem` SHALL expose a public method to set the start-time-set flag
- `AutoTestMainActivity` startup loading SHALL use this method and SHALL NOT use `Field.setAccessible` on `ResultItem` internals

#### Scenario: Flag restored from database

- **WHEN** a persisted result item with `isStartTimeSet = true` is loaded
- **THEN** the reconstructed `ResultItem` SHALL report `isStartTimeSet()` as true via the public setter path

### Requirement: Report file IO outside startup critical path

The system SHALL NOT create the report directory and file synchronously on the main thread during `AutoTestMainActivity.onCreate`.

- The report directory and `FileOutputStream` SHALL be created lazily on the first report write that needs them, through the shared synchronized accessor `AutoTestMainActivity.ensureReportStream()`
- Every report write site (`recordMessage` and the case-execution `postValue` overloads in `BaseTestCase`) SHALL obtain the stream via this accessor and guard against a null stream; direct reads of the static stream field for writing are prohibited
- `recordMessage` observable behavior (LiveData message posting plus file append) SHALL be unchanged

#### Scenario: First report write initializes the stream

- **WHEN** any report write site is invoked for the first time
- **THEN** the report directory and file SHALL be created at that point via `ensureReportStream()`
- **AND** the message SHALL be appended to the file as before

### Requirement: Static reference cleanup on destroy

The system SHALL release static view and stream references when the main activity is destroyed.

- On `onDestroy`, `AutoTestMainActivity` SHALL null the static `llMessage` reference and close plus null the static `FileOutputStream` if present
- Message posting after destroy SHALL NOT crash due to the released references

#### Scenario: Activity destroyed then recreated

- **WHEN** the main activity is destroyed and later recreated
- **THEN** the previous activity's views and streams SHALL NOT remain reachable through static fields
- **AND** the new instance SHALL re-bind the references it needs

### Requirement: ViewHolder-based expandable list binding

The system SHALL bind group and child views of `MyExpandableListAdapter` through a ViewHolder held in the view tag, avoiding repeated `findViewById` on recycled views.

- `getGroupView` and `getChildView` SHALL create the ViewHolder only when `convertView` is null and reuse it otherwise
- Rendered content SHALL be identical to the previous implementation

#### Scenario: Scrolling a long list

- **WHEN** the expandable list scrolls and views are recycled
- **THEN** recycled views SHALL be re-bound without additional `findViewById` calls
- **AND** displayed text, icons, and colors SHALL match the data
