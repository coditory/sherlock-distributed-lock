package com.coditory.sherlock.mongo.reactor

import com.coditory.sherlock.mongo.MongoIndexCreationSpec
import com.coditory.sherlock.mongo.MongoLockStorageSpec

class ReactorMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesReactorMongoSherlock {}