package com.george.data.mongo

import com.mongodb.MongoClient
import org.bson.BsonDocument
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.json.JsonParseException
import org.bson.types.ObjectId

class MongoDataService(mongoClient: MongoClient, database: String) {

    private val database = mongoClient.getDatabase(database)

    companion object {
        val mongoDataService = MongoDataService(
            MongoClient(),
            "ktor_db"
        )

    }

    fun allFromCollection(collection:String) : MutableList<Map<String, Any>> {
        val mongoResult  = database.getCollection(collection, Document::class.java)
        val result = ArrayList<Map<String, Any>>()
        mongoResult.find().forEach { doc ->
            val asMap : Map<String,Any> = mongoDocumentToMap(doc)
            result.add(asMap)
        }
        return result
    }

    fun getDocumentById(collection: String, id: String?): Map<String, Any>? {
        if (!ObjectId.isValid(id)) {
            return null
        }
        val document = database.getCollection(collection)
            .find(Document("_id", ObjectId(id)))
        if (document?.first() != null) {
            return mongoDocumentToMap(document.first())
        }
        return null
    }

    fun saveNewDocument(collection: String, document: String): String {
        return try {
            val bsonDocument = BsonDocument.parse(document)
            // we create the id ourselves
            bsonDocument.remove("_id")
            val oid = ObjectId()
            bsonDocument["_id"] = BsonObjectId(oid)
            database.getCollection(collection, BsonDocument::class.java)
                .insertOne(bsonDocument)
            oid.toHexString()
        } catch (ex: JsonParseException) {
            "Invalid JSON: ${ex.localizedMessage}"
        }
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