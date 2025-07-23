# Am I Looking At Something?
[![License](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-blue.svg)](https://bit.ly/cc-by-nc-sa-40)

##### AILAS - a fork of [HWYLA](https://github.com/TehNut-Mods/HWYLA) by [TehNut](https://github.com/TehNut).

###### *This fork is permitted under the [CC BY-NC-SA 4.0](LICENSE.md) license. Usage of this mod is permitted in all modpacks.*

---

### Information For Developers

###### *TODO*

Waila plugins are discovered from the `fabric.mod.json` file. Simply add a `custom` object field
to that file with the following data:

```json
{
  "waila:plugins": {
    "id": "mymod:my_plugin",
    "initializer": "foo.bar.Baz"
  }
}
```

`waila:plugins` can also be an array of objects instead of a singular object. 

A `required` field can be added to specify mods required for that plugin to be loaded. It can either be a single string 
or an array of strings.
