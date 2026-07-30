# Changelog

## 1.1.3
- Mounted `/home` and `/spawn` teleports keep vehicles and passengers attached instead of leaving the client visually dismounted.
- `/home` now uses Minecraft's normal bed and respawn-anchor placement without consuming an anchor charge.
- Vehicle teleports check the vehicle's actual size and deny the command when the destination is obstructed.
- When decorations block the normal position beside a bed, mounted players can use one clear position directly above the bed. A covered bed still denies the teleport.

## 1.1.0
- Added JSON config system (`config/omwh.json`)
- All cooldown durations now configurable
- Command aliases now configurable (`homeCommand`, `spawnCommand`)
- All messages now configurable with § color code support
- Teleport effects (sound/particles) can be toggled independently

## 1.0.0
- Initial release
- `/home` and `/spawn` commands
- PvP, damage, join, and regular cooldowns
- Vehicle/mount teleport support
