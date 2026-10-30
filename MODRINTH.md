# OMWH — On My Way Home

OMWH gets you home without leaving your ride behind. Use `/home` to return to your bed or charged respawn anchor, or `/spawn` to head back to world spawn. Riding a horse or bringing a villager home in a boat? They make the trip with you.

Your home is Minecraft's own respawn point, so there's no separate home to set. Players don't need to install anything to join a server running OMWH.

*Home doesn't have to be fancy. A bed will do.*

![A bed at the bottom of a ravine](https://cdn.modrinth.com/data/25MUGzDH/images/c163474c1fb3711514529475097289e6bf5edd86.jpg)

## Bring the boat. And the villager.

Stay in your boat, minecart or mount and run `/home` or `/spawn`. OMWH brings the vehicle and its passengers along, checking that there's room for the group to arrive safely.

*One boat, one villager, one `/home`.*

![A player and villager in a boat with the home command entered](https://cdn.modrinth.com/data/25MUGzDH/images/4d4fdb070dc5a196c5ac6f0c29ab95c8cdb9dd00.jpg)

*Back at the bed, villager included.*

![The player, villager and boat at the saved-home destination](https://cdn.modrinth.com/data/25MUGzDH/images/dcfc8d39aed89561ce3523edcbd39a88e3005535.jpg)

If a destination isn't safe, OMWH stops the teleport. You can use `/home force` or `/spawn force` to skip safety and vehicle-size checks, but cooldowns and destination rules still apply. Force can't give you a missing home.

**Force is enabled for everyone by default.** OMWH has no permission nodes; server owners can turn force off for everyone in the config.

## Make it work for your world

Want a shorter wait between trips? No wait after joining? Different messages? Open `config/omwh.json` after the first launch and change the settings you want.

*The boat's ready. The join cooldown has other plans.*

![A join-cooldown warning while the player and villager wait in a boat](https://cdn.modrinth.com/data/25MUGzDH/images/f246fb44b41fd2b188591c3dc8ffc7113b1e4203.jpg)

For example, these settings give teleports a 15-second cooldown, remove the wait after joining and turn off force commands:

```json
{
  "regularCooldownSeconds": 15,
  "joinCooldownSeconds": 0,
  "enableForceOverride": false
}
```

These are a few settings, not a replacement for your whole config. Change the matching entries in your file, then **restart the server or reopen your single-player world**. OMWH doesn't have a reload command.

You can also change command names, combat and damage cooldowns, messages, sounds, particles and dimension routing. Cross-dimension homes are enabled by default. By default, `/spawn` sends you to Overworld spawn; enable the other dimension settings if you want it to keep you in the Nether, End or a modded dimension.

See the [configuration guide](https://github.com/ff-tech-xyz/omwh/blob/main/CONFIGURATION.md) for every setting and default, or the [behavior guide](https://github.com/ff-tech-xyz/omwh/blob/main/BEHAVIOR.md) for destination checks and passenger limits.

## Installation

For **Minecraft 26.3**, install OMWH and **Fabric API** with **Fabric Loader 0.19.5+** and **Java 25+**. Use [Fabric API 0.161.0](https://modrinth.com/mod/fabric-api/version/bNnaTiuM) or a later compatible release. Builds for older Minecraft versions are available under **Versions**.

- **Single-player:** put both JARs in your Fabric client's `mods` folder.
- **Multiplayer:** put both JARs in the server's `mods` folder. Players don't need OMWH or Fabric on their clients.

**Upgrading?** Remove the old OMWH JAR before adding the new one. Keep only one copy in `mods`.

[Source](https://github.com/ff-tech-xyz/omwh) · [Report an issue](https://github.com/ff-tech-xyz/omwh/issues) · [Discord](https://discord.gg/tZ6Hx2ETA3) · [PyreHaven](https://pyrehaven.xyz)
