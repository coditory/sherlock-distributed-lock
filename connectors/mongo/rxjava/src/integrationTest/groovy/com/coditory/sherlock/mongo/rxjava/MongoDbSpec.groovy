package com.coditory.sherlock.mongo.rxjava

import com.coditory.sherlock.mongo.MongoIndexCreationSpec
import com.coditory.sherlock.mongo.MongoLockStorageSpec

class RxMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesRxMongoSherlock {}

class RxMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesRxMongoSherlock {}