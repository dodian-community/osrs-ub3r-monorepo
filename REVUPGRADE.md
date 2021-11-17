# Rev Upgrade Reference
If you want to look at what I've done to make this compatible with OSRS #199, you can continue reading below. **Though note that there are a lot of things missing. Even compared to RS Mod 2 #194, there are things missing.**

## Changes Made
Anything not in this list should be same as the original RS Mod 2 #194 version.

- Updated interface ids/childs
  - [fixed-frame.cname.yml](/plugins/content/gameframe/src/main/resources/fixed-frame.cname.yml)
  - [interfaces.uiname.yml](/plugins/content/gameframe/src/main/resources/interfaces.uiname.yml)
- Updated protocol plugins
  - [player-updating](/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/update/desktop.plugin.kts)
  - [server-packets](/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/server/desktop.plugin.kts)
  - [login-packets](/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/login/login.plugin.kts)
    - Updated the CRC checks to match the #200 client
  - [client-packets](/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/client/desktop.plugin.kts)
