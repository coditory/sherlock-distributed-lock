package com.coditory.sherlock

import com.coditory.sherlock.mongo.MongoIndexCreationSpec
import com.coditory.sherlock.mongo.MongoLockStorageSpec

class ReactorMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesReactorMongoSherlock {}