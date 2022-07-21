### Version 1.1.2
- Add puns command.

### Version 1.1.1
- Add rule34 command.

### Version 1.1.0
- Migrate SQLite to MongoDB for database solution. This was done to ease off migrations and reduce server side 
  backups for .db files.
- Add KMongo as MongoDB wrapper to use coroutine features KMongo provides.
- Migrate Warn, Kick and Ban commands to use MongoDB instead of SQLite.
- Migrate functionality for Global Guild Config.
- Remove Exposed and SQLite related JDBC driver providing dependencies.
- Add repository structure to interact with MongoDB.
- General housekeeping to make codebase clean.

### Version 1.0.6
- Add Phishing link protection to scan for scam links and send a notification to that channel.

### Version 1.0.5
- Add Top.gg library to send total number of guilds.

### Version 1.0.4
- Fix slash commands registry
- Enhance Reboot, Steam and Poll command
- Add feature flag support for dynamic features toggle from gitlab
- Bundled noods.json resource to the package until better solution is made available
- Housekeeping and other optimizations

### Version 1.0.3
- Fix and enhance nudes command.

### Version 1.0.2
- Add nudes command (added by Mayank - mostwanted002)
- Update dependencies
- Suppressed greetings until new suitable solution is found.
- Minor code cleanup