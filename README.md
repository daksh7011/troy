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
    <h4 align="center">Troy is a feature rich discord bot developed in Kotlin for Discord Servers.</h4>
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

## Overview

Troy is a Discord bot being developed in kotlin using kord and kord extensions framework. Troy is mainly being developed
around fun and moderation commands.

## Invite

* [Invite Troy](https://discord.com/api/oauth2/authorize?client_id=871836869493661736&permissions=397820423367&scope=bot%20applications.commands)

## Permissions

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
* **Manage Messages** is needed for purge command where you need to delete messages in bulk.
* **Manage Threads** is needed for future commands related to thread creation and updating from Troy with handy
  commands.
* **Embed Links** is needed because Troy utilize Discord Embeds heavily and denying this permission will break so many
  commands.
* **Attach Files** is needed for upcoming images related commands.
* **Read Message** History is needed to fetch case numbers while moderation command gets fired.
* **Add Reactions** is self-explanatory.
* **Voice related permissions** are needed for music commands.

If any new permission is required, You can find an explanation here.

## Installation Guide

**Before you begin**

1. Make sure you have IntelliJ Idea setup and ready.
2. Clone Repository.
3. Change your directory to troy with `cd troy` after cloning.
4. Create a file named `.env` in root directory and fill it out as shown in
   `.example-env`.

After you are done with the prerequisites above, Just build FatJar or run Main class in
`Main.kt` file from your IDE.

Note: I will add a thorough guide to set up the bot on Linux, Windows or Mac system in near feature. Meanwhile, if
someone wants to cover this, Open an issue and submit a Merge Request.

## Contribution Guide

Please take a look at the [contributing](CONTRIBUTING.md) guidelines if you're interested in helping by any means.

Contribution to this project is not only limited to coding help, You can suggest a feature, help with docs, enhancements
ideas or even fix some typos. You are just an issue away. Don't hesitate to create an issue.

## License

[MIT License](LICENSE) Troy is available under terms of MIT license.

Copyright (c) 2024 TechnoWolf FOSS

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

## Links

[Issue Tracker](https://github.com/daksh7011/troy/issues)
