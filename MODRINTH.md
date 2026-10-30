# OMWH

OMWH adds server-side `/home` and `/spawn` commands that use Minecraft's own saved-home and world-spawn destinations. Players don't need to install anything on their clients.


*A bed marks the home destination.*

![Saved home](https://cdn.modrinth.com/data/25MUGzDH/images/c163474c1fb3711514529475097289e6bf5edd86.jpg)

## Commands

- `/home` goes to your bed, charged respawn anchor, or server-set respawn point.
- `/spawn` goes to the spawn destination for your current dimension, with optional fallback to Overworld spawn.
- `/home force` and `/spawn force` skip OMWH's destination-safety and vehicle-size checks. They still respect cooldowns, missing destinations, disabled routes, unavailable worlds, and teleport failures.

If you're riding a boat, minecart, horse, or another mount, OMWH moves the vehicle and its whole passenger group together. Normal teleports check support, fluids, hazards, collision, world borders, and the full mounted footprint before moving anything.


*Running /home while riding with a passenger.*

![Mounted home command](https://cdn.modrinth.com/data/25MUGzDH/images/4d4fdb070dc5a196c5ac6f0c29ab95c8cdb9dd00.jpg)

*The boat and its passenger group arrive together.*

![mounted arrival](https://cdn.modrinth.com/data/25MUGzDH/images/dcfc8d39aed89561ce3523edcbd39a88e3005535.jpg)

## Before you install

- **OMWH has no permission nodes.** Every player who can run `/home` can also run `/home force` and skip safety checks. Force is enabled by default and can only be turned off globally with `enableForceOverride`.
- **Cross-dimension `/home` is enabled by default** through `enableCrossDimensionTeleport`.
- **`/spawn` is Overworld-only by default.** Nether, End, and modded-dimension spawn destinations stay disabled until you enable them.
- **Configuration is read only at server startup.** OMWH has no reload command, so restart after editing `config/omwh.json`.
- **Upgrading? Delete the old OMWH jar first.** Two OMWH jars in `mods` stop Fabric Loader on a duplicate mod ID.
- Passenger groups are limited to 64 entities. Normal mounted searches refuse roots wider than 14 blocks or groups needing more than 16 blocks of clear height.


*The join cooldown blocks teleporting until the configured wait has passed.*

![Join cooldown](https://cdn.modrinth.com/data/25MUGzDH/images/f246fb44b41fd2b188591c3dc8ffc7113b1e4203.jpg)

## Server setup

Command names, cooldowns, messages, sounds, particles, force access, cross-dimension homes, and per-dimension spawn routing all live in `config/omwh.json`. Every field, default, and placeholder is documented in [CONFIGURATION.md](https://github.com/ff-tech-xyz/omwh/blob/main/CONFIGURATION.md).

The latest release targets **Minecraft 26.3** and needs Fabric Loader 0.19.5 or newer, [Fabric API 0.161.0](https://modrinth.com/mod/fabric-api/version/bNnaTiuM), and Java 25 or newer. Older files for 26.1.2 and 1.21.11 remain available under Versions. OMWH is server-side and also works in singleplayer.

## Links

- [Source code](https://github.com/ff-tech-xyz/omwh)
- [Issues and suggestions](https://github.com/ff-tech-xyz/omwh/issues)
- [PyreHaven Discord](https://discord.gg/tZ6Hx2ETA3)

Made by PyreHaven. Find out more about us at [PyreHaven.xyz](https://pyrehaven.xyz).
