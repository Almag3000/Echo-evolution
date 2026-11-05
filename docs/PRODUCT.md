# ECHØ Evolution — Product specification

## Core loop
1. Configure a seeded ecosystem.
2. Observe organisms seeking resources and reproducing.
3. Inspect population and genome traits.
4. Switch to Creator and inject an organism with chosen genes.
5. Compare the consequences over generations.

## Modes
- Observer: view world, step time, inspect population and generations.
- Creator: tune speed, vision, curiosity, aggression; spawn agents and food.

## Current implementation
Pure Kotlin deterministic simulation, Android Compose screen, two modes, genome mutation and basic metrics. Aggression is currently inert, and no neural network is implemented.

## Next milestones
- Continuous simulation with pause, 1x/4x/16x speeds
- Save/load versioned worlds, experiment list, comparison charts
- Agent inspection and lineage
- Neural controller with heritable weights and reproducible mutations
- Combat/cooperation mechanics, configurable climate
- UI localization, performance profiling and release signing

## Constraints
No external AI subscription or server required. All simulation state stays local. APK release is gated on CI success and device testing.
