/// <reference path="../pb_data/types.d.ts" />
migrate((app) => {
  const collection = app.findCollectionByNameOrId("pbc_1108966215")

  // update collection data
  unmarshal({
    "listRule": "@request.auth.id != \"\" &&\n@collection.storage_access.user_id ?= @request.auth.id &&\n@collection.storage_access.storage_id ?= storage_id"
  }, collection)

  // update field
  collection.fields.addAt(1, new Field({
    "cascadeDelete": false,
    "collectionId": "pbc_3307824350",
    "hidden": false,
    "id": "relation547097947",
    "maxSelect": 1,
    "minSelect": 0,
    "name": "storage_id",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "relation"
  }))

  // update field
  collection.fields.addAt(2, new Field({
    "cascadeDelete": false,
    "collectionId": "pbc_2743667331",
    "hidden": false,
    "id": "relation291094865",
    "maxSelect": 1,
    "minSelect": 0,
    "name": "container_id",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "relation"
  }))

  return app.save(collection)
}, (app) => {
  const collection = app.findCollectionByNameOrId("pbc_1108966215")

  // update collection data
  unmarshal({
    "listRule": null
  }, collection)

  // update field
  collection.fields.addAt(1, new Field({
    "cascadeDelete": false,
    "collectionId": "pbc_3307824350",
    "hidden": false,
    "id": "relation547097947",
    "maxSelect": 1,
    "minSelect": 0,
    "name": "storageID",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "relation"
  }))

  // update field
  collection.fields.addAt(2, new Field({
    "cascadeDelete": false,
    "collectionId": "pbc_2743667331",
    "hidden": false,
    "id": "relation291094865",
    "maxSelect": 1,
    "minSelect": 0,
    "name": "containerID",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "relation"
  }))

  return app.save(collection)
})
