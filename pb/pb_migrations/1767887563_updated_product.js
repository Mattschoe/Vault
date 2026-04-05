/// <reference path="../pb_data/types.d.ts" />
migrate((app) => {
  const collection = app.findCollectionByNameOrId("pbc_1108966215")

  // update collection data
  unmarshal({
    "listRule": "storage_id.users.id ?= @request.auth.id",
    "viewRule": "storage_id.users.id ?= @request.auth.id"
  }, collection)

  return app.save(collection)
}, (app) => {
  const collection = app.findCollectionByNameOrId("pbc_1108966215")

  // update collection data
  unmarshal({
    "listRule": "@request.auth.id != \"\" &&\n@collection.storage_access.user_id ?= @request.auth.id &&\n@collection.storage_access.storage_id ?= storage_id",
    "viewRule": null
  }, collection)

  return app.save(collection)
})
