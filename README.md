# ECHØ Evolution

Offline-first artificial life simulator for Android.

## Vision
Agents perceive their environment, spend energy, seek food, reproduce, and inherit mutated behavior. Outcomes emerge from simulation rather than scripted stories.

## Planned architecture
- `app`: Android UI (Kotlin, Jetpack Compose)
- `simulation-core`: pure Kotlin deterministic simulation
- `data`: local persistence (Room / versioned snapshots)
- `experiments`: reproducible seeds and comparative analytics

## Simulation v0 rules
- 2D bounded world, seeded deterministic RNG
- agents have position, energy, age, speed and perception radius
- food regenerates at configurable intervals
- each tick: perceive -> decide -> move -> consume -> metabolism -> reproduction/death
- bounded populations and resource caps
- future: inheritable neural controller, mutation and selection

## Delivery milestones
1. Compilable Android project and pure Kotlin core with deterministic tests
2. Live world renderer, pause/speed, inspection and save/load
3. Genomes, mutation, neural decisions and experiments
4. Profiling, accessibility, release APK and documentation

## Acceptance criteria for milestone 1
Identical seed and actions produce identical snapshots after N ticks; no Android dependencies in simulation core; unit tests cover energy, food, bounds, reproduction and extinction.

Status: project initialized; APK not yet built.
