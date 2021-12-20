# JsonEM (Json Entity Models)

Library for **modders**, **resource pack makers**, and **modpack makers** to create and edit entity models **WITH JSON** <br/>
*Does not work with OptiFine format!*

### Generate Reference Entity Models:
If you'd like to dump json versions of all entity models (vanilla or modded) so that you can edit them easily with resource packs or use them as examples, here's how: 
<br/>
- Launch the game, and then open the configuration file located at `.minecraft/config/jsonem.properties`.
- Edit the second line of the file to be `dump_models=true`.
- Re-launch the game. Navigate to the folder `.minecraft/jsonem_dump`.
- Within this folder you should find all registered entity models dumped as json, formatted like a resource pack.
- Use this format when editing or adding models with a resource pack.

### Edit Models in Blockbench:
To edit models in [Blockbench](https://www.blockbench.net/), install the plugin located in this repository.
- Download the file `jsonem_models.js` from this GitHub repository
- In Blockbench, navigate to `File > Plugins`, and at the top of the dialog click the icon for `Load Plugin from File`. Choose the `jsonem_models.js` file.
- Navigate to `File > New` and choose `JsonEM Java Entity Model` to begin editing.
- You can also `File > Open Model` to import a JsonEM json model file, such as those dumped from the game.

### For Resource Pack/Modpack Makers:
- Complete the steps above to dump all entity models from your game or modpack.
- When editing your resource pack/modpack resources, add entity model json files as they are formatted in the dumped model folder.

### For Modders:
JsonEM can be used to create TexturedModelData for your entities entirely using json. <br/>
This guide will demonstrate how to make the model for the **cube entity** in [**this tutorial**](https://fabricmc.net/wiki/tutorial:entity) with json.
- Include the mod as follows:
```gradle
TBD
```
- Register the entity model layer for your entity through JsonEM instead of Fabric API (Excludes the need for a code-generated TexturedModelData)
```java
void onInitializeClient() {
    [...]
    JsonEM.registerModelLayer(MODEL_CUBE_LAYER);
}
```
- Add a model file to your mod resources containing your entity's model data <br/>
**EX:** `assets/my_mod/models/entity/my_entity/layer.json`
```json
{
    "texture": {
        "width": 64,
        "height": 64
    },
    "bones": {
        "cube": {
            "transform": {
                "origin": [0, 0, 0]
            },
            "cuboids": [
                {
                    "uv": [0, 0],
                    "offset": [-6, 12, -6],
                    "dimensions": [12, 12, 12]
                }
            ]
        }
    }
}
```
- Make sure that the bone `"cube"` in the file above is being accessed with the same name in your entity model
```java
public CubeEntityModel(ModelPart modelPart) {
    this.base = modelPart.getChild("cube"); // The original tutorial used an unspecified field called EntityModelPartNames.CUBE
}
```