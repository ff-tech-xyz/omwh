# Changelog

## 1.1.3
- Fixed `/home` moving mounted players and vehicles upward, sometimes onto roofs, when there was not enough room at the home.
- Fixed mounted `/home` and `/spawn` teleports sometimes leaving clients out of sync with the vehicle and passengers.
- Tightened `/spawn` placement rules so it chooses nearby safe ground with enough support and clear space for the player or vehicle.

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
