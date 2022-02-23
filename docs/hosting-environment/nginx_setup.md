# Setting Up NGINX
This is designed with using NGINX as a reverse proxy in mind, so it's assumed that this will be done in the hosting environment. **This documentation will try to cater around beginners as well, so if you're already experienced with NGINX, you might know a lot or even all of this.**

The central server and game/world server(s) can each go on separate servers. Can also use a separate server for the NGINX instance too. Although for now it's assumed that everything is hosted on the same server instance.

---

### Useful Information

#### RS Client's Js5 & Game Port
The RuneScape client will by default connect to `43594` for the **game** part and `443` for the **Js5** part. However, changing the `game build` (*param 15 in **jav_config.ws***) to something other than 0 will have the client connect to other ports.

*It's worth nothing though that Jagex uses `game build` to specify environment for their worlds.*
- 0 = production
- 1 = release candidate
- 2 = quality assurance
- 3 = work in progress
- 4 = local
- 5 = unknown (`-wti` suffix is applied)

*It's also probably worth noting that `game build` is used in player updating to force admin+ to be visible in game for production worlds (`game build = 0`).*

So after changing the `game build` to something other than 0, the client will assume the port to connect to is `WORLD_ID + 40000` for **game** and `WORLD_ID + 50000` for **Js5**. So for a world with id of `301`, game port would be `40301` and Js5 port would be `50301`.

**This is really only useful if you intend to add mobile support, where you can't change the sourcecode. Otherwise, you may as well just modify the client to support the port in a more convenient way.**

---

### Configuring NGINX
I would recommend installing [Certbot][certbot-url] on the server along with [NGINX][nginx-url], so you can generate SSL certificates for free with convenience, using [Let's Encrypt][letsencrypt-url].

Remember that all `<>` indicates placeholders for values you have to change accordingly.

1. Create a file called `central-server.conf` in `/etc/nginx/sites-available` (default NGINX config location), with following content:
```nginx
server {
    listen 80;

    server_name central.example.com;

    location / {
        proxy_pass http://127.0.0.1:<CENTRAL_SERVER_HTTP_PORT>/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
2. Create a file per world called `game-worlds.conf` in same directory as step 1, with following content (one block like this per world):
```nginx
server {
    listen 80;

    server_name world<WORLD-ID>.example.com;

    location / {
        proxy_pass http://127.0.0.1:<GAME_SERVER_HTTP_PORT>/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /gamepack.jar {
        proxy_pass http://127.0.0.1:<CENTRAL_SERVER_HTTP_PORT>/gamepack.jar;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
3. Now run following commands:
```
$ cd /etc/nginx/sites-enabled && sudo ln -s /etc/nginx/sites-avaialble/central-server.conf
$ cd /etc/nginx/sites-enabled && sudo ln -s /etc/nginx/sites-available/game-worlds.conf
$ sudo service nginx restart
```
4. NGINX should now be configured to proxy the HTTP traffic to the right application instances now.
5. Now it would be wise to generate an SSL certificate for the central server and each of the world servers. Do the following command to generate an SSL certificate:
```
$ sudo certbot --nginx -d central.example.com
$ sudo certbot --nginx -d world<WORLD_ID>.example.com
```
First time you generate a cert, you'll be asked to provide some information.
Each cert you generate will ask you if you want to redirect HTTP traffic to HTTPS. I would personally recommend enabling forceful redirect to HTTPS.

That should be it for configuring the NGINX server. And since we specified the `location /gamepack.jar` for the game world's config, it will proxy all requests to the central server, so the game world's HTTP server don't need to serve this and keep its own copy of the `gamepack.jar` file.


[certbot-url]: https://certbot.eff.org/
[nginx-url]: https://www.nginx.com/
[letsencrypt-url]: https://letsencrypt.org/
