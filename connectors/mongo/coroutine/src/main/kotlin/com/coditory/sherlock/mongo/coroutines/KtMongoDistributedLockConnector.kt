package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.LockId
import com.coditory.sherlock.LockRequest
import com.coditory.sherlock.OwnerId
import com.coditory.sherlock.SherlockException
import com.coditory.sherlock.coroutines.KtDistributedLockConnector
import com.coditory.sherlock.mongo.MongoDistributedLock
import com.coditory.sherlock.mongo.MongoDistributedLock.fromDocument
import com.coditory.sherlock.mongo.MongoDistributedLock.fromLockRequest
import com.coditory.sherlock.mongo.MongoDistributedLockQueries.queryAcquired
import com.coditory.sherlock.mongo.MongoDistributedLockQueries.queryAcquiredOrReleased
import com.coditory.sherlock.mongo.MongoDistributedLockQueries.queryById
import com.coditory.sherlock.mongo.MongoDistributedLockQueries.queryReleased
import com.mongodb.MongoCommandException
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.BsonDocument
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Clock
import java.time.Instant

internal class KtMongoDistributedLockConnector(
    collection: MongoCollection<Document>,
    private val clock: Clock,
) : KtDistributedLockConnector {
    private val collectionInitializer = KtMongoCollectionInitializer(collection)

    override suspend fun initialize() {
        try {
            collectionInitializer.getInitializedCollection()
        } catch (e: Throwable) {
            throw SherlockException("Could not initialize Mongo collection", e)
        }
    }

    override suspend fun acquire(lockRequest: LockRequest): Boolean {
        val now = now()
        return try {
            upsert(
                queryReleased(lockRequest.lockId, now),
                fromLockRequest(lockRequest, now),
            )
        } catch (e: Throwable) {
            throw SherlockException("Could not acquire lock: $lockRequest", e)
        }
    }

    override suspend fun acquireOrProlong(lockRequest: LockRequest): Boolean {
        val now = now()
        return try {
            upsert(
                queryAcquiredOrReleased(lockRequest.lockId, lockRequest.ownerId, now),
                fromLockRequest(lockRequest, now),
            )
        } catch (e: Throwable) {
            throw SherlockException("Could not acquire or prolong lock: $lockRequest", e)
        }
    }

    override suspend fun forceAcquire(lockRequest: LockRequest): Boolean {
        return try {
            upsert(
                queryById(lockRequest.lockId),
                fromLockRequest(lockRequest, now()),
            )
        } catch (e: Throwable) {
            throw SherlockException("Could not acquire or prolong lock: $lockRequest", e)
        }
    }

    override suspend fun release(
        lockId: LockId,
        ownerId: OwnerId,
    ): Boolean {
        return try {
            delete(queryAcquired(lockId, ownerId))
        } catch (e: Throwable) {
            throw SherlockException("Could not release lock: " + lockId.value + ", owner: " + ownerId, e)
        }
    }

    override suspend fun forceRelease(lockId: LockId): Boolean {
        return try {
            delete(queryById(lockId))
        } catch (e: Throwable) {
            throw SherlockException("Could not force release lock: " + lockId.value, e)
        }
    }

    override suspend fun forceReleaseAll(): Boolean {
        return try {
            deleteAll()
        } catch (e: Throwable) {
            throw SherlockException("Could not force release all locks", e)
        }
    }

    private suspend fun deleteAll(): Boolean {
        val result = getCollection().deleteMany(BsonDocument())
        return result.deletedCount > 0
    }

    private suspend fun delete(query: Bson): Boolean {
        val deleted = getCollection().findOneAndDelete(query)
        return deleted != null && fromDocument(deleted).isActive(now())
    }

    private suspend fun upsert(
        query: Bson,
        lock: MongoDistributedLock,
    ): Boolean {
        val documentToUpsert = lock.toDocument()
        return try {
            val current =
                getCollection()
                    .findOneAndReplace(query, documentToUpsert, upsertOptions)
            lock.hasSameOwner(current)
        } catch (exception: MongoCommandException) {
            if (exception.errorCode != DUPLICATE_KEY_ERROR_CODE) {
                throw exception
            }
            false
        }
    }

    private fun now(): Instant {
        return clock.instant()
    }

    private suspend fun getCollection(): MongoCollection<Document> {
        return collectionInitializer.getInitializedCollection()
    }

    companion object {
        private const val DUPLICATE_KEY_ERROR_CODE = 11000
        private val upsertOptions =
            FindOneAndReplaceOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER)
    }
}
