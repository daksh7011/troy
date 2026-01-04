<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h1>
        <br>
        <a href="#">
            <img src="https://gitlab.com/technowolf/troy/-/raw/master/assets/Troy.png"
            alt="Troy" width="200"></a>
        <br>
        Troy - Discord Bot
        <br>
    </h1>
    <h4 align="center">Troy is a feature-rich discord bot developed in Kotlin for Discord Servers.</h4>
</div>
<div align="center">
    <a href="https://github.com/daksh7011/troy/blob/develop/LICENSE" target="_blank">
        <img src="https://img.shields.io/badge/license-MIT-brightgreen.svg" alt="License: MIT">
    </a>
    <a href="https://makeapullrequest.com" target="_blank">
        <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat" alt="Make a MR">
    </a>
    <a href="https://github.com/daksh7011/troy/actions/workflows/master.yml">
        <img alt="pipeline status" src="https://github.com/daksh7011/troy/actions/workflows/master.yml/badge.svg" />
    </a>
    <a href="https://www.paypal.me/daksh7011" target="_blank">
        <img src="https://img.shields.io/badge/$-donate-ff69b4.svg?maxAge=2592000&amp;style=flat" alt="donate">
    </a>
    <br>
    <br>
</div>

## 🔍 Overview

Troy is a feature-rich Discord bot developed in Kotlin using the Kord and KordEx frameworks. The bot is designed to enhance Discord server experiences through a variety of fun
interactions, moderation tools, and utility commands.

### ✨ Key Features

- **Fun Commands**: Engage your community with entertaining commands like memes, jokes, facts, dictionary lookups, and more
- **Moderation Tools**: Efficiently manage your server with commands for banning, kicking, warning, and message pruning
- **Utility Functions**: Access useful tools like avatar display, Steam information, and custom polls
- **Safety Features**: Automatic phishing link detection to protect your community
- **NSFW Content**: Age-restricted commands for adult-oriented servers (can be disabled)
- **Customization**: Configure the bot to suit your server's specific needs

Troy is actively maintained and regularly updated with new features and improvements.

## 📚 Docs

- **[Contributing Guidelines](CONTRIBUTING.md)**: Guide for contributing to the project

## 📨 Invite

* [Invite Troy](https://discord.com/api/oauth2/authorize?client_id=871836869493661736&permissions=397820423367&scope=bot%20applications.commands)

## 🔑 Permissions

Troy needs several permissions to do what it is supposed to do. Every permission Troy needs is explained below.

Please note, Removing any of these permissions may break one or many commands.

* **View Audit Log** is self-explanatory
* **Manage Roles** is needed to mute members, Give them extra roles Troy needs this permission.
* **Kick Members** is self-explanatory.
* **Ban Members** is self-explanatory.
* **Create Instant Invite** is needed when you want to create an invitation link whenever you are feeling lazy.
* **Manage Nickname** is needed to manage nicknames across your server.
* **Change Nickname** is needed to change other's nickname.
* **View Channels** is self-explanatory
* **Read Message** is self-explanatory.
* **Send Message** is self-explanatory.
* **Public Threads** is self-explanatory.
* **Private Threads** is self-explanatory.
* **Send message in Threads** is self-explanatory.
* **Manage Messages** is needed for the purge command where you need to delete messages in bulk.
* **Manage Threads** is needed for future commands related to thread creation and updating from Troy with handy
  commands.
* **Embed Links** is needed because Troy uses Discord Embeds heavily and denying this permission will break so many
  commands.
* **Attach Files** is needed for upcoming images related commands.
* **Read Message** History is needed to fetch case numbers while the moderation commands get fired.
* **Add Reactions** is self-explanatory.
* **Voice-related permissions** are needed for music commands.

If any new permission is required, You can find an explanation here.

## 🚀 Installation Guide

### Prerequisites

- JDK 17 or higher
- Kotlin 2.2 or higher
- MongoDB (for data storage)
- Discord Bot Token

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/daksh7011/troy.git
   cd troy
   ```

2. **Configure Environment Variables**
   Create a file named `.env` in the root directory with the following variables:
   ```
   TOKEN=your_discord_bot_token
   PREFIX=!
   IS_DEBUG=false
   ```
   Note: this is not an exhaustive list. Please check the [.example-env](.example-env) file for all the tokens you need to run the bot

3. **Build and Run**

   **Using Gradle:**
   ```bash
   ./gradlew build
   ./gradlew run
   ```

   **Using Docker:**
   ```bash
   docker build -t troy-bot .
   docker run -d --name troy-bot --env-file .env troy-bot
   ```

   **Using IDE:**
   Open the project in IntelliJ IDEA and run the `App.kt` file.

### ⚙️ Configuration Options

- `ownerId`: Discord user ID (snowflake) of the bot owner
- `girlfriendUser`: Discord user ID (snowflake) of the girlfriend user
- `prefix`: Command prefix for text commands
- `token`: Your Discord bot token
- `botId`: Discord bot ID
- `isDebug`: Enable debug mode (true or false)
- `sentryDsn`: Sentry DSN for error tracking
- `testGuildId`: Discord server ID for testing
- `redditUsername`: Reddit username for API access
- `redditPassword`: Reddit password
- `redditAppId`: Reddit application ID
- `redditAppSecret`: Reddit application secret
- `redditUserAgent`: Reddit user agent string
- `stackoverflowApiKey`: Stack Overflow API key
- `geniusApiKey`: Genius API key (optional)
- `openWeatherKey`: OpenWeather API key
- `topGgToken`: Top.gg API token
- `mongoUrl`: MongoDB connection URL (without username and password)
- `mongoUserName`: MongoDB username
- `mongoPassword`: MongoDB password
- `owlDictToken`: Token for OwlDict.info dictionary service

## 🏗️ Technical Architecture

Troy is built with modern Kotlin practices and follows a structured architecture:

### 💻 Technology Stack

- **Kotlin**: Primary programming language
- **Kord**: Discord API wrapper for Kotlin
- **KordEx**: Extension framework for Kord that provides command handling
- **MongoDB**: Database for storing configuration and moderation data
- **Koin**: Dependency injection framework
- **Sentry**: Optional error tracking and monitoring

### 📂 Project Structure

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

### 🛠️ Key Features Implementation

- **Phishing Protection**: Automatically scans messages for known phishing domains and warns users
- **Moderation System**: Tracks warnings, kicks, and bans in the database for accountability
- **Command Framework**: Uses KordEx for slash command support with permission handling
- **Dependency Injection**: Uses Koin for managing dependencies and services

### 🧩 Design Patterns

- **Repository Pattern**: Used for data access abstraction
- **Dependency Injection**: For loose coupling between components
- **Event-Driven Architecture**: Responds to Discord events through event handlers

## 👥 Contribution Guide

Please take a look at the [contributing](CONTRIBUTING.md) guidelines if you're interested in helping by any means.

Contribution to this project is not only limited to coding help. You can suggest a feature, help with documentation, propose enhancements, or even fix typos. You are just an issue
away. Don't hesitate to create an issue.

## 📝 License

[MIT License](LICENSE) Troy is available under terms of MIT license.

Copyright (c) 2025 TechnoWolf FOSS

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 🔗 Links

[Issue Tracker](https://github.com/daksh7011/troy/issues)
