package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.mongo.MongoIndexCreationSpec
import com.coditory.sherlock.mongo.MongoLockStorageSpec

class KtMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesKtMongoSherlock {}

class KtMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesKtMongoSherlock {}
