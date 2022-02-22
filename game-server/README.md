# Dodian Game World
This is the game server/world server instance.

You'll have to create a `world-config.yml` file in the data directory similar to:
```yaml
id: 999
activity: Local Development
flags: [BETA]
location: UNITED_KINGDOM
```

You'll also have to create `config.yml` in the root directory similar to:
```yaml
name: "Ub3r"
env: "development"
revision: 201
home:
  - 2606
  - 3102
  - 0
central-server: https://central.dodian.net/
```
