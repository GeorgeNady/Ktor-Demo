package com.george.data.mongo

import com.mongodb.MongoClient
import org.bson.Document
import org.bson.types.ObjectId

class MongoDataService(mongoClient: MongoClient, database: String) {

    private val database = mongoClient.getDatabase(database)

    fun allFromCollection(collection:String) : ArrayList<Map<String, Any>> {
        val mongoResult  = database.getCollection(collection, Document::class.java)
        val result = ArrayList<Map<String, Any>>()
        mongoResult.find().forEach { doc ->
            val asMap : Map<String,Any> = mongoDocumentToMap(doc)
            result.add(asMap)
        }
        return result
    }

    private fun mongoDocumentToMap(document: Document) : Map<String,Any> {
        val asMap : MutableMap<String,Any> = document.toMutableMap()
        if (asMap.containsKey("_id")) {
            val id = asMap.getValue("_id")
            if (id is ObjectId) {
                asMap.set("_id",id.toHexString())
            }
        }
        return asMap
    }

}