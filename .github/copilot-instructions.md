# GitHub Copilot Instructions

## Project Context
This is an Android application called HoraSolis built with Kotlin and Jetpack Compose. The app appears to be related to solar time and alarm management.

## Code Style Guidelines

### Kotlin/Android Specific
- Use Kotlin coding conventions and idiomatic Kotlin patterns
- Prefer `data class` for simple data holders
- Use `ImmutableList` and `persistentListOf()` from kotlinx-collections-immutable for state management
- Follow Android Architecture Components patterns (ViewModel, Repository, etc.)
- Use proper lifecycle-aware components

### Jetpack Compose Guidelines
- Always use `@Composable` functions for UI components
- Follow Compose naming conventions (PascalCase for composables)
- Use `Modifier` parameter as the first parameter with default value
- Prefer `remember` and `rememberSaveable` for state management
- Use `LazyColumn/LazyRow` for lists instead of `Column/Row` with `verticalScroll`
- Always provide `contentDescription` for accessibility
- Use Material 3 components (Material3) instead of Material 2
- Prefer `HorizontalDivider` over deprecated `Divider`

### Function Ordering
- Order functions from most general/global to most specific/private
- Place public composables and functions first
- Place private helper functions after the functions that call them
- Group related functions together
- Follow the pattern: public functions → internal functions → private functions
- Within each visibility level, order by usage dependency (callers before callees)

### Project Structure
- Follow the existing package structure: `ca.arnaud.horasolis.ui.*`
- Place UI components in appropriate packages (e.g., `ui.alarmmanager`)
- Use proper separation of concerns: UI, ViewModels, Repository, Data layers
- Keep composables focused on single responsibilities

### State Management
- Use immutable data structures for state
- Prefer `StateFlow` and `MutableStateFlow` for ViewModels
- Use `@PreviewLightDark` for composable previews
- Always include preview composables for UI components

### Performance & Best Practices
- Use `key` parameter in `LazyColumn` items for better performance
- Avoid recreating objects in composables - use `remember` when needed
- Use `Modifier.clickable` for clickable elements
- Prefer composition over inheritance
- Write clean, readable code with proper spacing and formatting

### Testing
- Write unit tests for business logic
- Use composable tests for UI components
- Mock external dependencies properly

## Specific Preferences
- When creating UI components, extract reusable parts into separate composables
- Always validate code changes and fix compilation errors
- Use meaningful variable and function names
- Add proper documentation for complex logic
- Follow Material Design principles for UI/UX

## Error Handling
- Handle edge cases gracefully
- Use proper exception handling
- Provide meaningful error messages
- Validate inputs appropriately

## Dependencies
- Use the project's existing dependency versions
- Prefer Jetpack libraries over third-party alternatives when available
- Only suggest new dependencies when absolutely necessary
