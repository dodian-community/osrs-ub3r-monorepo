# Setup RuneLite Plugin Hub
This is a guide on how to setup your own RuneLite plugin hub.

### Requirements
- HTTP Server
- Access to OpenSSL (which I guess most OSs has these days)
- A copy of RuneLite if you want to test your hub

### Generating & Verifying Keys
1. Run this command: `openssl req -x509 -newkey rsa:1024 -keyout key.pem -out cert.pem -days 365 -nodes`
2. That will give you a key.pem and cert.pem \
    - cert.pem will be what goes into your RuneLite copy as externalplugins.crt (just rename it)
    - key.pem is placed in the [util](/util)-module and named private.pem
3. Once you have these files, put them in [util/keys](/util/keys)-folder
4. Run the Gradle-task `plugin-hub`->`verifyKeys`
5. If all is ok, you should get a prompt saying "ALL OK! :)", if something failed \
    your keys aren't working, and something likely went wrong during generation.

### Downloading Plugins From RuneLite Plugin Hub
1. Make sure you've generated and validated your public and private key from above section.
2. Put the private.pem inside [util](/util)-folder.
3. Get RuneLite's `externalplugins.crt` and place that in the [util](/util)-folder as well.
4. Run the Gradle-task `plugin-hub`->`downloadPlugins`
5. That should give you a `output`-folder with the plugins and a manifest.js in it. This is what you upload on your http-server.

### Setting Up the Plugin Hub
1. Create a folder structure on your web server, refer to this structure: http://repo.runelite.net/plugins/
   - I used this structure: `/runelite/plugins/1.7.23/`, which is where you upload the content of the `output`-directory from step 5 in last section. 1.7.23 is the version of RuneLite I'm personally using, find yours!
2. Following my example, you use this plugin-hub url: https://example.com/runelite/plugins
