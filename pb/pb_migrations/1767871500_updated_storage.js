/// <reference path="../pb_data/types.d.ts" />
migrate((app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // update collection data
  unmarshal({
    "listRule": "@request.auth.id != \"\" && \n@collection.storage_access.user_id ?= @request.auth.id && \n@collection.storage_access.storage_id ?= id"
  }, collection)

  return app.save(collection)
}, (app) => {
  const collection = app.findCollectionByNameOrId("pbc_3307824350")

  // update collection data
  unmarshal({
    "listRule": ""
  }, collection)

  return app.save(collection)
})
