# Rithle

<p>
  <!--
  <a href="">
    <img alt="google-play" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/google-play_vector.svg">
  </a>
  <a href="">
    <img alt="f-droid" height="56" src="https://cdn.theclashfruit.me/devins-badges/assets/cozy-minimal/f-droid_vector.svg">
  </a>
  -->
  <a href="https://github.com/TheClashFruit/Rithle/releases">
    <img alt="github" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/available/github_vector.svg" />
  </a>
  <a href="https://discord.gg/CWEApqJ6rc">
    <img alt="Discord" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy-minimal/social/discord-singular_vector.svg">
  </a>
</p>

An app for browsing Modrinth on your Android phone.

## Screenshots

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://cdn.theclashfruit.me/apps/me.theclashfruit.rithle/screenshots-dark.png">
  <source media="(prefers-color-scheme: light)" srcset="https://cdn.theclashfruit.me/apps/me.theclashfruit.rithle/screenshots-light.png">

  <img alt="Screenshots" src="https://cdn.theclashfruit.me/apps/me.theclashfruit.rithle/screenshots-light.png">
</picture>

## Contributing

If you'd like to help improve Rithle, please take a look at our [CONTRIBUTING.md](CONTRIBUTING.md)
for some guidelines. A huge thank you for your support.

### Translations

Contributions to translations for both the app interface and store metadata are highly welcome.

* **Metadata**: Located in `metadata/{languageCode}` (e.g., `metadata/en-US`).
* **App Strings**: Located in `app/src/main/res/values/strings.xml`. To add a new language, create a new directory like `values-{languageCode}`.

## Backend Services (Mainly OAuth)

Since I don't want to leak the client secret in releases I host a simple service for Rithle to
handle OAuth, the source code for that is available over at
[TheClashFruit/RithleApi](https://github.com/TheClashFruit/RithleApi).

## License

```
Rithle, Android app for Modrinth written in Kotlin.
Copyright (C) 2022 - 2026 TheClashFruit

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```