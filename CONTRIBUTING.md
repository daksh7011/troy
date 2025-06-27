# Contributing to Troy

Thank you for considering contributing to Troy! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
    - [Project Setup](#project-setup)
    - [Development Environment](#development-environment)
- [Development Workflow](#development-workflow)
    - [Branching Strategy](#branching-strategy)
    - [Commit Guidelines](#commit-guidelines)
    - [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
    - [Kotlin Style Guide](#kotlin-style-guide)
    - [Documentation](#documentation)
    - [Testing](#testing)
- [Dependency Management](#dependency-management)
- [Building and Testing](#building-and-testing)
- [Code Quality Tools](#code-quality-tools)
- [Project Structure](#project-structure)
- [Common Issues and Solutions](#common-issues-and-solutions)

## Code of Conduct

Please read our [Code of Conduct](CODE_OF_CONDUCT.md) before contributing to the project.

## Getting Started

### Project Setup

1. **Fork the Repository**
   ```bash
   git clone https://github.com/yourusername/troy.git
   cd troy
   ```

2. **Configure Environment Variables**
   Create a file named `.env` in the root directory with the following variables:
   ```
   TOKEN=your_discord_bot_token
   PREFIX=!
   IS_DEBUG=true
   ```
   Check [Readme.md](README.md) for detailed example for environment variables

### Development Environment

- JDK 17 or higher
- Kotlin 2.2 or higher
- MongoDB (for data storage)
- IntelliJ IDEA (recommended)

## Development Workflow

### Branching Strategy

- `master` - Production-ready code
- `develop` - Main development branch
- Feature branches should be created from `develop` and named according to the feature being implemented (e.g., `feature/add-new-command`)
- Bug fix branches should be named with a `fix/` prefix (e.g., `fix/issue-123`)
- Dependency upgrades, maintenance jobs should be named with `chore/` or `maintenance/` prefix (e.g., `chore/kotlin-upgrade` or `maintenance/fix-breaking-changes`)

### Commit Guidelines

- Use clear, descriptive commit messages
- Reference issue numbers, PR numbers in commit messages when applicable
- Keep commits focused on a single change
- No warnings or deprecated APIs are allowed
- Avoid using try-catch; use Kotlin alternatives instead. i.e., `runCatching`
- Double bangs (!!) are NOT allowed in any condition, even with null checks
- Use null checks for defensive programming
- Follow the project's naming conventions
- Try to rebase your branch if your branch lags behind develop.

### Pull Request Process

1. Create a pull request against the `develop` branch
2. Ensure all tests pass and code quality checks succeed
3. Update documentation if necessary
4. Request a review from at least one maintainer
5. Address any feedback from reviewers

## Coding Standards

### Kotlin Style Guide

- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Kotlin 2.2 features when applicable
- No Java code is allowed; refactor any Java code to Kotlin
- Maximum line length is 180 characters
- Use four (4) spaces for indentation, No tabs are allowed.
- Organize imports according to the project's import ordering rules

### Documentation

- Include KDoc for all public APIs and classes not located under `internal` package
- Document the purpose, parameters, and return values of public methods
- Keep documentation up-to-date with code changes

Example of proper KDoc:

```kotlin
/**
 * Performs a specific task with the given parameters.
 *
 * @param param1 Description of the first parameter
 * @param param2 Description of the second parameter
 * @return Description of the return value
 * @throws SomeException When something goes wrong
 */
fun someFunction(param1: String, param2: Int): Result {
    // Implementation
}
```

### Testing

- Write unit tests for new functionality
- Ensure existing tests pass before submitting a pull request
- Use JUnit for testing
- Use MockK for mocking dependencies

## Dependency Management

- All dependencies should be managed in the `gradle/libs.versions.toml` file
- When adding a new dependency, add it to the appropriate section in the versions catalog
- Keep dependencies up-to-date, but ensure compatibility

Example of adding a dependency:

```toml
[versions]
new-library = "1.0.0"

[libraries]
new-library = { module = "com.example:new-library", version.ref = "new-library" }
```

## Building and Testing

- Build the project: `./gradlew build`
- Run tests: `./gradlew test`
- Run the application: `./gradlew run`
- Build Docker image: `docker build -t troy-bot .`

## Code Quality Tools

### Detekt

The project uses [Detekt](https://github.com/detekt/detekt) for static code analysis. Always run Detekt before submitting a pull request:

```bash
./gradlew detekt
```

## Project Structure

The project follows a structured architecture:

- **commands/**: Contains all bot commands organized by category
    - **config/**: Configuration-related commands
    - **funstuff/**: Entertainment and fun commands
    - **misc/**: Miscellaneous utility commands
    - **mod/**: Moderation commands
    - **nsfw/**: Age-restricted commands
- **core/**: Core bot functionality and initialization
- **data/**: Data models and repositories
- **utils/**: Utility functions and helpers
- **apiModels/**: Data models for external API integrations

## Common Issues and Solutions

### Dependency Resolution Issues

If you encounter dependency resolution issues, try:

```bash
./gradlew --refresh-dependencies
```

### MongoDB Connection Issues

Ensure your MongoDB instance is running (if local) and the values in your `.env` file is correct.

### Discord API Rate Limiting

Be aware of Discord API rate limits when testing commands that make multiple API calls.

---

Thank you for contributing to Troy! Your efforts help make this Discord bot better for everyone.
