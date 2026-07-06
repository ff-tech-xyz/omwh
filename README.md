# OMWH — On My Way Home

OMWH adds two simple teleport commands to Fabric: `/home` for returning to your bed or respawn anchor, and `/spawn` for getting back to the spawn point in the dimension you are already in.

It is built for servers that want convenient travel without turning `/home` and `/spawn` into free cross-dimension warps. Players can get unstuck, regroup, or head back to a safe point, while server owners keep control over cooldowns, messages, command names, and teleport effects.

Built by [PyreHaven](https://pyrehaven.xyz) and used on the [PyreHaven Minecraft Server](https://pyrehaven.xyz).

Questions or bug reports? Join the [PyreHaven Discord](https://discord.gg/tZ6Hx2ETA3) or open an issue on GitHub.

---

## What the commands do

### `/home`

Teleports a player to their current respawn point. That means their bed in the Overworld or their respawn anchor in the Nether, as long as the point is valid.

`/home` stays in the player's current dimension. It will not move someone from the Nether back to an Overworld bed.

### `/spawn`

Teleports a player to the spawn point for the dimension they are already in:

- Overworld: the world spawn
- Nether: the Nether spawn point
- End: the obsidian platform

Like `/home`, `/spawn` does not cross dimensions.

### Mounts and passengers

If a player is riding something when they teleport, OMWH brings the ride with them. Horses, boats, pigs, striders, and similar mounts stay attached. Passengers inside the same vehicle, including other players, come along too.

---

## Why server owners use it

- Keeps `/home` and `/spawn` useful without making them global warp commands
- Works server-side only, so players do not need to install anything on their client
- Lets admins tune cooldowns for normal use, combat, recent damage, and joining the server
- Supports custom command names, such as `/h` and `/s`
- Lets you rewrite messages and use the `{time}` placeholder for cooldown warnings
- Can play a sound and show particles when a teleport finishes
- Uses safe teleport checks so players are not dropped into bad spots

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for the supported Minecraft version.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api).
3. Drop the OMWH jar into your `mods/` folder.
4. Start the game or server once. OMWH creates `config/omwh.json` automatically.

OMWH works in singleplayer and on dedicated servers. On servers, install it on the server only; clients do not need the mod.

---

## Configuration

Edit `config/omwh.json` after the first launch.

| Field | Default | What it controls |
|---|---:|---|
| `homeCommand` | `"home"` | Command name for `/home` |
| `spawnCommand` | `"spawn"` | Command name for `/spawn` |
| `regularCooldownSeconds` | `30` | Time between normal teleports |
| `pvpCooldownSeconds` | `45` | Cooldown after PvP |
| `damageCooldownSeconds` | `10` | Cooldown after taking non-player damage |
| `joinCooldownSeconds` | `30` | Cooldown after joining the server |
| `playTeleportSound` | `true` | Plays the Enderman teleport sound on arrival |
| `spawnTeleportParticles` | `true` | Shows portal particles on arrival |
| Message fields | see config | Text shown to players, with Minecraft color codes and `{time}` support |

Set any cooldown to `0` to disable that cooldown.

---

## Links

- [PyreHaven Discord](https://discord.gg/tZ6Hx2ETA3)
- [PyreHaven Website](https://pyrehaven.xyz)
- [Source code](https://github.com/ff-tech-xyz/omwh)
- [Issues and suggestions](https://github.com/ff-tech-xyz/omwh/issues)

---

## License

MIT. See [LICENSE](LICENSE).
