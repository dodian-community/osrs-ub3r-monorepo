# World Server Configuration

This will cover setting up a world server for your players. I'll try to keep it simple to understand, so it's possible
to follow for beginners as well as professionals.

---

1. Create a JAR of the `game-server`-module using the Gradle-task `build -> jar`
2. Take the `game-server-<VERSION>-all.jar` and upload it to your server somewhere that makes sense.
   Example `/homes/rsps/world1/` (this example assumes you're hosting your applications under the Linux user `rsps`)
3. Create `config.yml` file in the root directory (same directory as the `game-server.jar`-file), with this content: [*(refer to table below for values)*](#configuration-value-explanations)
```yaml
name: RS Mod
env: prod
revision: 201.1
home:
  - 3200
  - 3200
  - 0
central-server: http://127.0.0.1
host: 127.0.0.1
port: 43594
data-path: ./data
plugin-path: ./plugins
```
4. Create `world-config.yml` in the server's data directory. [*(refer to table below for values)*](#configuration-value-explanations)
```yaml
id: 1
activity: "-"
flags: []
location: UNITED_KINGDOM
```
5. If you don't want the game server to pull the cache from the central server, you may provide it manually by putting it in the `./data/cache/packed`-directory
6. You will have to manually provide the `xteas.json` and put it in `./data/cache/`-directory

**TODO:**
- Optionally pull the `xteas.json` from the central server
- Optionally Pull RSA key file (`./data/rsa/key.pem`) from central server
- Generate the name files on server start if they aren't already present
- Generate the RSA key file (`key.pem`) on server start if one isn't already present
- Install plugin config files automatically on server start
  - Will only install missing config files
  - Will not override existing config files, because they may be changed
- Ability to provide its own `jav_config.ws` so things like [game build](/docs/hosting-environment/nginx_setup.md#rs-clients-js5--game-port) can be configured per world.

## Configuration Value Explanations
If a value in these config files has a default value, and you want to use that default value, you don't need to specify that value in the respective config file.

### `/config.yml`
| Property       | Default Value       | Valid Values          | Description                       |
|----------------|---------------------|-----------------------|-----------------------------------|
| name           | `RS Mod`            | *any string*          | Server's name                     |
| env            | `prod`              | `dev`, `test`, `prod` | Hosting environment               |
| revision       | `none`              | *any number*          | Client revision                   |
| home           | `3200, 3200`        | *up to 3 coords*      | X, Y & height of home location    |
| central-server | `http://127.0.0.1/` | *any url*             | Public URL for the central server |
| host           | `127.0.0.1`         | *any IP*              | IP to bind the application to     |
| port           | `43594`             | `0-65353`             | Game port                         |
| data-path      | `./data`            | *any path*            | Path to the server's data dir     |
| plugin-path    | `./plugins`         | *any path*            | Path to the server's plugins dir  |

### `/data/world-config.yml`
| Property | Default Value    | Valid Values      | Description          |
|----------|------------------|-------------------|----------------------|
| id       | `none`           | *any number*      | World's ID           |
| activity | `-`              | *any string*      | World's activity     |
| flags    | `[]`             | *world flag list* | List of world flags  |
| location | `UNITED_KINGDOM` | *world location*  | Valid world location |

**World Flags:**
- `MEMBERS_ONLY`
- `PVP`
- `BETA`
- `DEADMAN`

**World Locations:**
- `UNITED_STATES`
- `UNITED_KINGDOM`
- `AUSTRALIA`
- `GERMANY`
