# RuneLite Plugin Hub
Follow the steps below if you want to set up your own RuneLite Plugin Hub and activate it in your copy of RuneLite.

### Requirements
- HTTP Server
- Access to OpenSSL (which I guess most operating systems has these days)
- A copy of RuneLite if you want to test your hub

### Generating & Verifying Keys
Code: [KeyValidator.kt](/src/main/kotlin/org/rsmod/util/runelite/KeyValidator.kt)
1. Run this command in the [keys][keys-folder]-folder: `openssl req -x509 -newkey rsa:1024 -keyout private.pem -out externalplugins.crt -days 365 -nodes`
2. Once you have these files, put them in [keys][keys-folder]-folder
3. Run the Gradle-task `plugin-hub`->`verifyKeys`
4. If all is ok, you should get a prompt saying `Everything works, your keys are ready to use!`. If something failed your keys aren't working, and something likely went wrong during generation.

### Downloading Plugins From RuneLite Plugin Hub
Code: [PluginDownloader.kt](/src/main/kotlin/org/rsmod/util/runelite/PluginDownloader.kt)
1. Make sure you've generated and validated your public and private key from above section.
2. Make sure to create your `wanted-plugins.yml` config inside the [plugin-hub][plugin-hub-folder]-folder
3. Get RuneLite's `externalplugins.crt` and place that in the [plugin-hub][plugin-hub-folder]-folder as well.
4. Run the Gradle-task `plugin-hub`->`downloadPlugins`
5. That should give you a `output`-folder inside [plugin-hub][plugin-hub-folder]-folder with the plugins and a manifest.js in it. This is what you upload on your http-server.

### Setting Up the Plugin Hub
1. Create a folder structure on your web server, refer to this structure: http://repo.runelite.net/plugins/
    - I used this structure: `/runelite/plugins/1.7.23/`, which is where you upload the content of the `output`-directory from step 5 in last section. 1.7.23 is the version of RuneLite I'm personally using, find yours!
2. Following my example, you use this plugin-hub url: https://example.com/runelite/plugins

[keys-folder]: /plugin-hub/keys
[plugin-hub-folder]: /plugin-hub
