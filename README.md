# GorsokSpawn

A small, clean Paper plugin: `/spawn` with a warmup, a cooldown and fully editable messages.
Demo project by **Gorsok** (Fiverr: @gorsok).

## Features
- `/setspawn` saves the spawn at your position.
- `/spawn` teleports after a short warmup with a countdown above the hotbar.
- The teleport is cancelled if the player moves or takes damage (each can be turned off).
- Cooldown between uses, with bypass permissions for staff.
- Sound and particle effects (can be turned off).
- Every message is editable in `config.yml` (MiniMessage colors and gradients).
- `/gspawn reload` reloads the config without restarting the server.

## Commands and permissions
| Command | Permission | Default |
| --- | --- | --- |
| `/spawn` | `gspawn.use` | everyone |
| `/setspawn` | `gspawn.set` | op |
| `/gspawn reload` | `gspawn.admin` | op |
| (no warmup) | `gspawn.bypass.warmup` | op |
| (no cooldown) | `gspawn.bypass.cooldown` | op |

## Install
1. Put `GorsokSpawn-1.0.0.jar` in your server's `plugins` folder.
2. Restart the server.
3. Stand where you want the spawn and type `/setspawn`.
4. Edit `plugins/GorsokSpawn/config.yml` if you want, then `/gspawn reload`.

## Build from source
Requires internet access the first time (Gradle downloads its tools and Java 25 if needed).
- Windows: `gradlew.bat build`
- Linux / macOS: `./gradlew build`

The plugin is in `build/libs/`. Tested on Paper 26.2 (Java 25 to build; the plugin runs on Java 21+); change the `paper-api` version in `build.gradle.kts` to match another server.

## Need a custom plugin?
This is a demo of my work. I build custom Paper plugins from your idea: a clear plan and a fixed price first, then a tested plugin with full source code.
Order on Fiverr (English or French): https://www.fiverr.com/gorsok/turn-your-idea-into-a-custom-minecraft-plugin-for-your-paper-server
