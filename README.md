# OMWH

OMWH adds `/home` and `/spawn` to Fabric servers without turning them into cross-dimension warps.

- `/home` returns you to a valid bed or respawn anchor in your current dimension.
- `/spawn` finds safe ground near the current dimension's spawn.
- Mounts, vehicles, and their passengers travel with you when there is room.
- Server owners can configure cooldowns, command names, messages, sounds, and particles.
- Players do not need to install OMWH on their clients.

Built by [PyreHaven](https://pyrehaven.xyz).

## Teleport rules

`/home` uses Minecraft's normal placement around your respawn point. If you are mounted and the vehicle cannot fit beside an uncovered bed, OMWH may place the group directly above it. Invalid, blocked, or cross-dimension homes are refused.

`/spawn` searches near the spawn point for solid ground with enough clear space for the player or vehicle. In the End, it uses the obsidian platform.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api).
3. Put the OMWH jar in the `mods` folder.
4. Start the server or game once to create `config/omwh.json`.

OMWH also works in singleplayer.

## Configuration

Edit `config/omwh.json` after the first launch.

| Field | Default | Purpose |
|---|---:|---|
| `homeCommand` | `"home"` | Name of the home command |
| `spawnCommand` | `"spawn"` | Name of the spawn command |
| `regularCooldownSeconds` | `30` | Cooldown between normal teleports |
| `pvpCooldownSeconds` | `45` | Cooldown after PvP |
| `damageCooldownSeconds` | `10` | Cooldown after other damage |
| `joinCooldownSeconds` | `30` | Cooldown after joining |
| `playTeleportSound` | `true` | Play a sound after teleporting |
| `spawnTeleportParticles` | `true` | Show particles after teleporting |
| Message fields | See config | Text shown to players; supports Minecraft color codes and `{time}` |

Set a cooldown to `0` to disable it.

## Links

- [Download on Modrinth](https://modrinth.com/mod/omwh)
- [Issues and suggestions](https://github.com/ff-tech-xyz/omwh/issues)
- [PyreHaven Discord](https://discord.gg/tZ6Hx2ETA3)

## License

[MIT](LICENSE)
