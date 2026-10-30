# OMWH — On My Way Home

OMWH adds `/home` and `/spawn` commands that bring your boat, minecart or mount and its passengers with you. Bring a villager home in a boat without leaving either behind.

![A bed at the bottom of a ravine](docs/images/saved-home.jpg)

## Commands

- `/home` returns you to your bed or charged respawn anchor, including across dimensions by default.
- `/spawn` takes you to Overworld spawn by default. Server owners can configure separate destinations for other dimensions.
- `/home force` and `/spawn force` skip safety and vehicle-size checks. Cooldowns still apply. Force commands are available to everyone by default; server owners can disable them.

OMWH checks that the destination is safe and has room for your whole group before teleporting.

*Use `/home` without getting out of the boat.*

![A player and villager in a boat with the home command entered](docs/images/mounted-home-command.jpg)

*The boat and its passengers arrive together.*

![The player, villager and boat at the saved-home destination](docs/images/mounted-arrival.jpg)

## Configuration

Edit `config/omwh.json` to change cooldowns, command names, messages, sounds, particles and dimension settings. Set a cooldown to `0` to disable it, then restart the server or reopen your single-player world to apply changes.

![A join-cooldown warning while the player and villager wait in a boat](docs/images/join-cooldown.jpg)

See the [configuration guide](CONFIGURATION.md) for all settings and defaults, or the [behavior guide](BEHAVIOR.md) for destination and passenger rules.

## Installation

For **OMWH 1.2.1**, you need:

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API for Minecraft 26.3 (the 1.2.1 release uses 0.161.0)
- Java 25 or newer

Download OMWH from [Modrinth](https://modrinth.com/mod/omwh).

- **Single-player:** put OMWH and Fabric API in your Fabric client's `mods` folder.
- **Multiplayer:** put both JARs in the server's `mods` folder. Players don't need either mod on their clients.

## Links

- [PyreHaven](https://pyrehaven.xyz)
- [Discord](https://discord.gg/tZ6Hx2ETA3)
- [Report an issue](https://github.com/ff-tech-xyz/omwh/issues)
- [Building and architecture](ARCHITECTURE.md)

## License

[CC0 1.0 Universal](LICENSE)
