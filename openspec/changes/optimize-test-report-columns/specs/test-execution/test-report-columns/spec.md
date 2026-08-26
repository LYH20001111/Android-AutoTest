## Purpose

Defines the column structure for the test report summary table and the settings-page record summary, distinguishing between "total defined test cases" and "total executed test cases" for each test item.

## ADDED Requirements

### Requirement: Report summary shows case total and executed total columns

The test report summary table SHALL display two distinct numeric columns for each test item:
- "案例总数" (Case Total): the number of test cases defined in the test item
- "测试总数" (Test Total): the number of test cases that were actually executed

#### Scenario: Report summary displays both columns
- **WHEN** user opens the test report summary
- **THEN** the summary table SHALL include a "案例总数" column showing the number of defined cases per test item
- **AND** the "测试总数" column (previously named "案例总数") SHALL show the number of executed cases per test item

#### Scenario: Settings page record summary displays both columns
- **WHEN** user opens "查看测试记录汇总" in the settings page
- **THEN** the summary table SHALL include a "案例总数" column showing the number of defined cases per test item
- **AND** the "测试总数" column (previously named "案例总数") SHALL show the number of executed cases per test item

### Requirement: English report localization

The English version of the report SHALL display the equivalent column names:
- "Case Total" for the new column (number of defined cases)
- "Executed Num" for the renamed column (number of executed cases)

#### Scenario: English report summary displays both columns
- **WHEN** the report is generated in English
- **THEN** the summary table SHALL include a "Case Total" column
- **AND** the "Executed Num" column SHALL replace the previous "Total Num" column name

### Requirement: Data accuracy for executed test count

The "测试总数" / "Executed Num" SHALL count only the test cases that were actually executed during the test run, excluding any cases that were defined but not run.

#### Scenario: Executed count differs from defined count
- **WHEN** a test item defines 10 cases but only 8 were executed (e.g., due to test interruption)
- **THEN** "案例总数" SHALL show 10
- **AND** "测试总数" SHALL show 8