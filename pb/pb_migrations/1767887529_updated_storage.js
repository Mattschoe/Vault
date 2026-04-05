/// <reference path="../pb_data/types.d.ts" />
migrate((app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // update collection data
  unmarshal({
    "listRule": "users.id ?= @request.auth.id",
    "viewRule": "users.id ?= @request.auth.id"
  }, collection)

  // add field
  collection.fields.addAt(3, new Field({
    "cascadeDelete": false,
    "collectionId": "_pb_users_auth_",
    "hidden": false,
    "id": "relation344172009",
    "maxSelect": 999,
    "minSelect": 0,
    "name": "users",
    "presentable": false,
    "required": false,
    "system": false,
    "type": "relation"
  }))

  return app.save(collection)
}, (app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // update collection data
  unmarshal({
    "listRule": "@request.auth.id != \"\" && \n@collection.storage_access.user_id ?= @request.auth.id && \n@collection.storage_access.storage_id ?= id",
    "viewRule": ""
  }, collection)

  // remove field
  collection.fields.removeById("relation344172009")

  return app.save(collection)
})
