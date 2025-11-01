# Mobby

Mobby is a fork of [KittenForever](https://github.com/LeMinaw/kittenforever) updated to work with newer Minecraft versions (1.20.x to 1.21.x). With Mobby, you can "mini-fy" mobs to stop them from ageing or free them to resume their natural growth. The plugin also tracks the mobs you’ve mini-fied or freed, allowing you to view their status and location at any time. All credit goes to [LeMinaw](https://www.github.com/LeMinaw) for creating the base plugin.

Grab the latest version from the [Releases Page](https://www.github.com/plushycat/mobby/releases)

---

## 🌟 Features
- **Mini-fy Mobs**: Prevent mobs from ageing using configurable items.
- **Free Mobs**: Resume mob growth when desired.
- **Mob Tracking**: Keep track of mobs you’ve mini-fied or freed, including their name, type, status, and location.
- **Periodic Updates**: Mob locations are updated every 60 seconds by default (configurable).
- **Persistent Records**: Tracked mobs are saved to disk and persist across server restarts.
- **Simple Commands**: Easily manage the plugin with `/mobby` commands.

---

## 📋 Commands
### `/mobby reload`
- **Description**: Reloads the plugin configuration.
- **Permission**: `mobby.reload`

### `/mobby list`
- **Description**: Displays a list of mobs you’ve mini-fied or freed.
  - **Hover**: See the mob’s type, UUID, and current status (age-locked or not).
  - **Location**: View the last known location of the mob.
- **Permission**: `mobby.list`

---

## ⚙️ Configuration
The plugin generates a `config.yml` file on first run. Below are the default settings:

```yaml
items:
  stopGrowth:
    before: AMETHYST_SHARD
    after: AIR
    amount: 1
  resumeGrowth:
    before: HONEY_BOTTLE
    after: GLASS_BOTTLE
    amount: 1

tracking:
  update-interval-seconds: 60
  save-on-change: true
```

### Key Settings
- **Items**:
  - `before`: The item required to mini-fy or free a mob.
  - `after`: The item given back after the action (set to `AIR` for nothing).
  - `amount`: The number of items consumed per action.
- **Tracking**:
  - `update-interval-seconds`: How often mob locations are updated (default: 60 seconds).
  - `save-on-change`: Whether to save mob records immediately after changes.

---

## 🐾 How It Works
1. **Mini-fy a Mob**:
   - Right-click a baby mob with the configured item (e.g., `AMETHYST_SHARD`).
   - The mob will stop ageing, and you’ll see a particle effect.
2. **Free a Mob**:
   - Right-click a mini-fied mob with the configured item (e.g., `HONEY_BOTTLE`).
   - The mob will resume ageing, and you’ll see a happy particle effect.
3. **Track Your Mobs**:
   - Use `/mobby list` to view all mobs you’ve mini-fied or freed.
   - Hover over a mob’s name to see its type, UUID, and status.

---

## 📜 License
Mobby is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2025 com.plushycat.mobby

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 💡 Contributing
We welcome contributions! To get started:
1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Submit a pull request with a clear description of your changes.

---

## 🌍 Connect
- **Issues**: Found a bug? [Report it here](https://github.com/plushycat/mobby/issues).

Happy mini-fying! 🐾