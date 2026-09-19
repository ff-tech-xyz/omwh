# OMWH architecture

OMWH is a small server-side Fabric mod. Production behavior stays in one Java package so ownership is visible without an extra service or framework layer.

```text
omwh/
├── .github/workflows/ci.yml
├── .gitignore
├── ARCHITECTURE.md
├── BEHAVIOR.md
├── CHANGELOG.md
├── CONFIGURATION.md
├── LICENSE
├── README.md
├── build.gradle
├── gradle.properties
├── gradlew
├── settings.gradle
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/xyz/pyrehaven/omwh/
    │   │   ├── Omwh.java
    │   │   ├── OmwhConfig.java
    │   │   ├── OmwhCommands.java
    │   │   ├── Cooldowns.java
    │   │   ├── HomeDestination.java
    │   │   ├── SpawnDestination.java
    │   │   ├── DestinationSafety.java
    │   │   └── TeleportService.java
    │   └── resources/
    │       ├── fabric.mod.json
    │       └── assets/omwh/icon.png
    └── test/java/xyz/pyrehaven/omwh/
        ├── ConfigTest.java
        ├── CommandsAndCooldownsTest.java
        ├── DestinationsTest.java
        └── TeleportServiceTest.java
```

## Dependency direction

`Omwh` wires the runtime. `OmwhCommands` coordinates policy owners but does not duplicate destination or movement logic. `HomeDestination` and `SpawnDestination` produce prepared destinations through `DestinationSafety`. Only `TeleportService` mutates the root/passenger tree. `Cooldowns` and `OmwhConfig` do not depend on command scheduling.

Tests exercise dependency-free policy seams and the production scheduler. A policy seam is useful only when the corresponding Minecraft adapter calls it; narrow fixture collaborators belong in tests rather than production overloads.

## Contributor notes

Minecraft 26.3's saved-respawn and End-arrival algorithms are version-coupled boundaries. Recheck the mapped vanilla reads, order, and side effects on a Minecraft update. Work and chunk constants sit beside the production loops they bound, and traversal/state-machine comments record invariants that are not obvious from local syntax.

Before submitting a change, run:

```bash
./gradlew clean regressionTest build
git diff --check
```

Do not add generated Gradle output to the source tree.
