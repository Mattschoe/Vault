/// <reference path="../pb_data/types.d.ts" />
migrate((app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // remove field
  collection.fields.removeById("bool2512237629")

  return app.save(collection)
}, (app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // add field
  collection.fields.addAt(2, new Field({
    "hidden": false,
    "id": "bool2512237629",
    "name": "is_dirty",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "bool"
  }))

  return app.save(collection)
})
