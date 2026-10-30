# OMWH — On My Way Home

Get home without leaving your ride behind. OMWH adds `/home` and `/spawn` to Minecraft, bringing your boat, minecart or mount along with you. Friends and mobs can come too, as long as they're riding as passengers.

It's a server-side Fabric mod, so players don't need to install anything to join your server.

## A bed is all you need

Sleep in a bed or set your respawn point at a charged respawn anchor, then use `/home` whenever you're ready to head back. OMWH uses your Minecraft respawn point, so there's no separate home to set. Use `/spawn` to return to world spawn instead.

![A bed at the bottom of a ravine](docs/images/saved-home.jpg)

*Home doesn't have to be fancy.*

## Bring everyone along

Found a villager you want to bring home? Get in the boat with them, then run `/home`. The boat and its passengers make the trip together. The same works with minecarts and mounts, and you can use `/spawn` while riding too.

![A player and villager in a boat with the home command entered](docs/images/mounted-home-command.jpg)

*No need to get out first.*

![The player, villager and boat at the saved-home destination](docs/images/mounted-arrival.jpg)

*Back at the bed, villager included.*

OMWH checks the destination before moving you and your ride. If it isn't safe or your vehicle won't fit, the teleport stops. `/home force` and `/spawn force` let you skip those checks, but cooldowns still apply. Force is available to everyone by default; server owners can turn it off.

## Home can be in another dimension

Exploring the Nether? `/home` can take you straight back to your Overworld bed. If your home is a charged respawn anchor in the Nether, you can travel the other way too. Your ride and passengers come with you.

Cross-dimension travel is enabled by default, and `/spawn` takes you to Overworld spawn from any dimension. Server owners can disable travel between dimensions or keep `/spawn` within the player's current dimension. The [configuration guide](CONFIGURATION.md#dimension-routing) explains those options, including the End's arrival point.

## A short wait between trips

Cooldowns keep teleporting useful without making it an instant escape from a fight. By default, you'll wait:

- **30 seconds** after a successful teleport before using `/home` or `/spawn` again.
- **45 seconds** after PvP damage, for both players involved.
- **10 seconds** after other damage, such as a mob attack or a fall.
- **30 seconds** after joining a server or opening a single-player world.

![A join-cooldown warning while the player and villager wait in a boat](docs/images/join-cooldown.jpg)

*The message tells you how long is left.*

## Make it fit your world

Launch the server or open your single-player world once to generate `config/omwh.json`. Open that file to adjust cooldowns, rename commands, change messages, sounds and particles, or choose how travel between dimensions works.

For example, change `regularCooldownSeconds` to `15` for a shorter wait between trips, or `0` to remove that cooldown. Save the file, then restart the server or reopen your world to apply the change.

See the [configuration guide](CONFIGURATION.md) for every setting and default. The [behavior guide](BEHAVIOR.md) covers the finer details of destinations and passenger groups.

## Installation

For **OMWH 1.2.1**, you need:

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API for Minecraft 26.3 (the 1.2.1 release uses 0.161.0)
- Java 25 or newer

Download OMWH from [Modrinth](https://modrinth.com/mod/omwh).

- **Single-player:** put OMWH and Fabric API in your Fabric client's `mods` folder.
- **Multiplayer:** put both JARs in the server's `mods` folder.

## Links

- [PyreHaven](https://pyrehaven.xyz)
- [Discord](https://discord.gg/tZ6Hx2ETA3)
- [Report an issue](https://github.com/ff-tech-xyz/omwh/issues)
- [Building and architecture](ARCHITECTURE.md)

## License

[CC0 1.0 Universal](LICENSE)
