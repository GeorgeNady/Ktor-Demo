package com.george.mongodb

import com.george.utiles.ConsoleHelper
import com.george.utiles.Constants.POSTS_COLLECTION
import com.george.utiles.ExtensionFunctionHelper.toJson
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.*
import org.bson.conversions.Bson
import com.mongodb.client.model.Updates.*
import org.bson.json.JsonParseException
import org.bson.types.ObjectId

/**
 * # Resources
 * **You can see the visit this web site for more information** [link](https://polyglot-phil.com/kotlin/mongo/rest/ktor/2019/03/24/kotlin-mongodb-rest-webservice.html)
 * */
class MongoDataService(mongoClient: MongoClient, database: String) {

    private val database = mongoClient.getDatabase(database)

    fun allFromCollection(collection:String) : MutableList<Map<String, Any>> {
        val mongoResult  = database.getCollection(collection, Document::class.java)
        val result = ArrayList<Map<String, Any>>()
        mongoResult.find().forEach { doc ->
            val asMap : Map<String,Any> = mongoDocumentToMap(doc)
            result.add(asMap)
        }
        return result
    }

    fun countAllDocsFromCollection(collection: String): Int {
        val mongoResult = database.getCollection(collection, Document::class.java)
        return mongoResult.find().count()
    }

    fun getAllDocFromCollectionPaginated(collection: String, limit: Int, page: Int): MutableList<Map<String, Any>> {
        val mongoResult = database.getCollection(collection, Document::class.java)
        val result = ArrayList<Map<String, Any>>()
        val skip = (page - 1) * limit
        mongoResult
            .find()
            .skip(skip)
            .limit(limit)
            .sort(BasicDBObject("modified_at", -1))
            .forEach { doc ->
                val asMap: Map<String, Any> = mongoDocumentToMap(doc)
                result.add(asMap)
            }
        return result
    }

    fun countMyDocsFromCollection(email: String): Int {
        val mongoResult = database.getCollection(POSTS_COLLECTION, Document::class.java)
        return mongoResult.find().count { doc ->
            doc.getValue("user_email") == email
        }
    }

    fun getMyDocFromCollectionPaginated(limit: Int, page: Int, email: String): MutableList<Map<String, Any>> {
        val mongoResult = database.getCollection(POSTS_COLLECTION, Document::class.java)
        val result = ArrayList<Map<String, Any>>()
        val skip = (page - 1) * limit
        mongoResult
            .find()
            .skip(skip)
            .limit(limit)
            .sort(BasicDBObject("modified_at", -1))
            .filter { doc ->
                doc.getValue("user_email") == email
            }
            .forEach { doc ->
                val asMap: Map<String, Any> = mongoDocumentToMap(doc)
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

    fun getDocumentByEmail(collection: String, email: String?): Map<String, Any>? {
        val document = database.getCollection(collection)
            .find(Document("email", email))
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

    fun updateExistingDocument(
        collection: String,
        id: String?,
        document: String
    ): Pair<Int, String> {
        try {
            if (!ObjectId.isValid(id)) {
                return Pair(0, "ID not found")
            }
            val bsonDocument = BsonDocument.parse(document)
            bsonDocument.remove("_id")
            val filter = BsonDocument("_id", BsonObjectId(ObjectId(id)))
            val updatedValues =
                database.getCollection(collection, BsonDocument::class.java)
                    .replaceOne(filter, bsonDocument).modifiedCount
            return if (updatedValues < 1) {
                Pair(0, "ID not found")
            } else {
                Pair(1, "success")
            }
        } catch (ex: JsonParseException) {
            return Pair(-1, "Invalid JSON: ${ex.localizedMessage}")
        }
    }

    fun deleteDocument(collection: String, id: String?): Pair<Int, String> {
        if (!ObjectId.isValid(id)) {
            return Pair(0, "ID not found")
        }
        val filter = BsonDocument("_id", BsonObjectId(ObjectId(id)))
        val updatedValues = database.getCollection(collection)
            .deleteOne(filter).deletedCount
        return if (updatedValues < 1) {
            Pair(0, "ID not found")
        } else {
            Pair(1, "success")
        }
    }

    companion object {
        val mongoDataService = MongoDataService(MongoClient(),"ktor_db")
    }

}