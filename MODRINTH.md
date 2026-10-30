# OMWH — On My Way Home

OMWH is a server-side Fabric mod that gets you home without leaving your ride behind. Use `/home` to return to your bed or charged respawn anchor, or `/spawn` to head back to world spawn. Riding a horse or bringing a villager home in a boat? They make the trip with you.

Your home is Minecraft's own respawn point, so there's no separate home to set. Players don't need to install anything to join a server running OMWH.

*Home doesn't have to be fancy. A bed will do.*

![A bed at the bottom of a ravine](https://cdn.modrinth.com/data/25MUGzDH/images/c163474c1fb3711514529475097289e6bf5edd86.jpg)

## Bring the boat. And the villager.

Stay in your boat, minecart or mount and run `/home` or `/spawn`. OMWH brings the vehicle and its passengers along, checking that there's room for the group to arrive safely.

*Use `/home` without getting out of the boat.*

![A player and villager in a boat with the home command entered](https://cdn.modrinth.com/data/25MUGzDH/images/4d4fdb070dc5a196c5ac6f0c29ab95c8cdb9dd00.jpg)

*Back at the bed, villager included.*

![The player, villager and boat at the saved-home destination](https://cdn.modrinth.com/data/25MUGzDH/images/dcfc8d39aed89561ce3523edcbd39a88e3005535.jpg)

If a destination isn't safe, OMWH stops the teleport. You can use `/home force` or `/spawn force` to skip safety and vehicle-size checks, but cooldowns and destination rules still apply. Force can't give you a missing home.

**Force is enabled for everyone by default.** OMWH has no permission nodes; server owners can turn force off for everyone in the config.

## Cross-dimension teleporting

Cross-dimension teleporting is enabled by default. `/home` returns you to your saved respawn point even when it's in another dimension. For example, you can return from the Nether to your Overworld bed, or from the Overworld to your charged respawn anchor in the Nether. Your vehicle and passengers come with you.

By default, `/spawn` takes you to Overworld spawn from any dimension. Enable `enableNetherSpawn`, `enableEndSpawn` or `enableModdedDimensionSpawn` to keep `/spawn` in that dimension instead. End routing uses Minecraft's End arrival point.

Set `enableCrossDimensionTeleport` to `false` to stop travel between dimensions. `/home` then requires your home to be in your current dimension. `/spawn` still works where that dimension's spawn route is enabled; otherwise it is blocked rather than sending you to the Overworld. `enableOverworldSpawn` controls access to Overworld spawn.

## Cooldowns

Cooldowns control how often players can teleport and prevent immediate escapes from combat. All four are enabled by default:

- **Regular — 30 seconds:** the wait after using `/home` or `/spawn`.
- **PvP — 45 seconds:** applies to both players when one damages the other.
- **Damage — 10 seconds:** applies after taking damage from other sources, such as mobs or falls.
- **Join — 30 seconds:** the wait after joining the server or opening a single-player world.

*Cooldown messages show the remaining wait.*

![A join-cooldown warning while the player and villager wait in a boat](https://cdn.modrinth.com/data/25MUGzDH/images/f246fb44b41fd2b188591c3dc8ffc7113b1e4203.jpg)

## Configuration

Edit `config/omwh.json` after the first launch to change cooldowns, command names, messages, sounds, particles and dimension routing. Set a cooldown to `0` to disable it.

For example, this changes the regular cooldown to 15 seconds:

```json
{
  "regularCooldownSeconds": 15
}
```

Change the matching entry in your file, then **restart the server or reopen your single-player world** to apply your settings.

See the [configuration guide](https://github.com/ff-tech-xyz/omwh/blob/main/CONFIGURATION.md) for every setting and default, or the [behavior guide](https://github.com/ff-tech-xyz/omwh/blob/main/BEHAVIOR.md) for destination checks and passenger limits.

## Installation

For **Minecraft 26.3**, install OMWH and **Fabric API** with **Fabric Loader 0.19.5+** and **Java 25+**. Use [Fabric API 0.161.0](https://modrinth.com/mod/fabric-api/version/bNnaTiuM) or a later compatible release. Builds for older Minecraft versions are available under **Versions**.

- **Single-player:** put both JARs in your Fabric client's `mods` folder.
- **Multiplayer:** put both JARs in the server's `mods` folder. Players don't need OMWH or Fabric on their clients.

**Upgrading?** Remove the old OMWH JAR before adding the new one. Keep only one copy in `mods`.

[Source](https://github.com/ff-tech-xyz/omwh) · [Report an issue](https://github.com/ff-tech-xyz/omwh/issues) · [Discord](https://discord.gg/tZ6Hx2ETA3) · [PyreHaven](https://pyrehaven.xyz)
