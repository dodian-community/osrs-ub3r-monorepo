# OSRS JavConfig
Documents what the `jav_config.ws` does and what each value means for your RSPS. Refer to the example file at the bottom of this document.

---

**Here is a list of the ones used in the game client:**
- `title` = Application frame title (probably not relevant for RuneLite clients)
- `codebase` = The hostname specifying which world server to connect to for both JS5 and game itself.
- `initial_jar` = The `gamepack.jar` provided by the world server. Used by RuneLite to download the injected client jar file.
- `initial_class` = The main class of the `gamepack.jar`-file.
- `param=1` = `useBufferedSockets` - Use buffered sockets (`0` or `1` for `false` or `true`), *will only not use buffered sockets if value is `0`*
- `param=2` = *can't find reference to this in client, not relevant for RSPS*
- `param=3` = `isMembersWorld` - `true` or `false` are the expected values. If value isn't `true`, it will always be false in the client.
- `param=4` = `clientType` - An int value specifying what client type this is. Presumably used to determine if this is a mobile or desktop client.
- `param=5` = `worldProperties` - The same number that is specified in the world list for its properties. Read about game worlds [here](/docs/osrs-specific/osrs_world.md) to get more information on this number.
- `param=6` = `clientLanguage` - A language ID for one of the hardcoded client languages. *(0-6)*
  - `0 = English (UK)`, `1 = German`, `2 = French`, `3 = Portuguese`, `4 = Dutch`, `5 = Spanish`, `6 = Spanish (Latin America)`
- `param=7` = `gameBuild` - Used for some field in the client that translates to a GameBuild object instance. Probably just an older or newer way of doing it than `param 15`.
  - 0 = Production, 1 = Release Candidate, 2 = Work In Progress, 3 = Build Live
  - Seems to be used in some directory path variables during client load/startup.
- `param=8` = Doesn't seem to be used for anything, but is a boolean value (`true` or `false`)
- `param=9` = Seems to be some access token or something, which is passed to the game server during login, just after sending the UUID.
  - This value is probably not too relevant for an RSPS.
- `param=10` = `studioGame` - Not entirely sure what this does exactly.
  - `0 = RS3`, `1 = Stellar Dawn`, `2 = Game 3`, `3 = Game 4`, `4 = Game 5`, `5 = OSRS`
  - Most likely a value they have in the client because the OSRS client share some code with other Jagex products. So you probably want to just keep this at `5`.
- `param=11` = `authUrl` - Just the base URL for Jagex' authentication API, which in the client is suffixed by `public/v1/games/YCfdbvr2pM1zUYMxJRexZY/play`, and it sends a GET request to this URL with some access token it gets from an environment variable `JX_ACCESS_TOKEN`.
  - Presumably this is something Jagex uses internally.
  - Seems to send some kind of authentication request when logging out, under certain conditions.
  - Can probably safely ignore this for RSPS.
- `param=12` = `worldId` - The ID of the initial world.
- `param=13` = `cookieDomain` - Used to set the cookie domain for cookies used by the game client.
  - Seems to be used for a server packet to set cookies for the client.
  - Client doesn't seem to do anything other than setting these cookies.
  - Probably not so relevant for RSPS.
- `param=14` = Unsure what this is, it's sent to the server just after `param 9` in the login protocol.
- `param=15` = `gameBuild` - Just a number representation of the GameBuild used for some other stuff in the client. OSRS officially uses 0 for all public player worlds (0 being production/live environment).
  - `0 = Production`, `1 = Release Candidate`, `2 = Quality Assurance`, `3 = Work In Progress`, `4 = Local`, `5 = Unknown (has a '-vti' suffix)`
  - If gameBuild is anything else than 0, the client will connect to the chosen `worldId + 40000` for game and `worldId + 50000` for Js5.
  - It also appears that when `gameBuild` is 0 (live/production world), admin and above can't be invisible.
- `param=16` = Doesn't seem to be used by anything in the OSRS client (has a `true` or `false` value)
- `param=17` = `worldListUrl` - The URL to the world list in a packed buffer format supported by the OSRS client. ([see this class](/common/src/main/kotlin/net/dodian/common/util/Extensions.kt) for encoding, *decoding would be just the same in reverse*)
- `param=18` = Some prefix for setting the document cookies, by default is empty. Probably not relevant for RSPS.
- `param=19` = Doesn't seem to be used by the OSRS desktop client, but Jagex has some `googleusercontent.com` hostname there, so presumably used by mobile client (maybe Steam client too(?))
- `param=20` = Doesn't seem to be used by the OSRS desktop client, but it seems to be some kind of token authentication API. Probably used by the Steam and/or mobile clients, if at all.
- `param=21` = *Unsure what this is, but it seems to be some client preference.*
- `param=22` = *Not sure what it does, but 0 is false, anything else is true. Seems to be enabling something in the region loading.*

Not all the above parameters seem to the relevant for the OSRS non-Steam desktop client. However, they can probably be relevant for both the Steam client and the mobile clients. Some or all the values from the `jav_config.ws`-files provided by Jagex that aren't listed above might also be relevant for OSRS Steam client and/or OSRS mobile clients. Most of them should be self-explanatory anyway though.

Now it's up to you to decide what's relevant to keep and change for your own server setup.

---

### FAQ
- **Q: Why is the file named `jav_config.ws` and what does it mean?** \
  *A: Unsure, might not be any good answers to that out there.*

---

### **Example jav_config.ws from OSRS:**
```dotenv
title=Old School RuneScape
adverturl=http://www.runescape.com/g=oldscape/bare_advert.ws
codebase=http://oldschool169.runescape.com/
cachedir=oldschool
storebase=0
initial_jar=gamepack_5288850.jar
initial_class=client.class
termsurl=http://www.jagex.com/g=oldscape/terms/terms.ws
privacyurl=http://www.jagex.com/g=oldscape/privacy/privacy.ws
viewerversion=124
win_sub_version=1
mac_sub_version=2
other_sub_version=2
browsercontrol_win_x86_jar=browsercontrol_0_-1928975093.jar
browsercontrol_win_amd64_jar=browsercontrol_1_1674545273.jar
download=2078308
window_preferredwidth=800
window_preferredheight=600
advert_height=96
applet_minwidth=765
applet_minheight=503
applet_maxwidth=5760
applet_maxheight=2160
msg=lang0=English
msg=tandc=This game is copyright © 1999 - 2022 Jagex Ltd.\Use of this game is subject to our ["http://www.runescape.com/terms/terms.ws"Terms and Conditions] and ["http://www.runescape.com/privacy/privacy.ws"Privacy Policy].
msg=options=Options
msg=language=Language
msg=changes_on_restart=Your changes will take effect when you next start this program.
msg=loading_app_resources=Loading application resources
msg=err_verify_bc64=Unable to verify browsercontrol64
msg=err_verify_bc=Unable to verify browsercontrol
msg=err_load_bc=Unable to load browsercontrol
msg=loading_app=Loading application
msg=err_create_target=Unable to create target applet
msg=err_create_advertising=Unable to create advertising
msg=err_save_file=Error saving file
msg=err_downloading=Error downloading
msg=ok=OK
msg=cancel=Cancel
msg=message=Message
msg=copy_paste_url=Please copy and paste the following URL into your web browser
msg=information=Information
msg=err_get_file=Error getting file
msg=new_version=Update available! You can now launch the client directly from the OldSchool website.\nGet the new version from the link on the OldSchool homepage: http://oldschool.runescape.com/
msg=new_version_linktext=Open OldSchool Homepage
msg=new_version_link=http://oldschool.runescape.com/
param=20=https://token-auth.production.jxp.aws.jagex.com/
param=16=false
param=15=0
param=12=469
param=5=16384
param=3=false
param=11=https://auth.jagex.com/
param=10=5
param=22=0
param=9=ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw
param=7=0
param=17=http://www.runescape.com/g=oldscape/slr.ws?order=LPWM
param=6=0
param=2=https://payments.jagex.com/
param=19=196515767263-1oo20deqm6edn7ujlihl6rpadk9drhva.apps.googleusercontent.com
param=13=.runescape.com
param=4=1
param=21=0
param=14=0
param=8=true
param=18=
param=1=1
```
