package com.coditory.sherlock

class RxMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesRxMongoSherlock {}

class RxMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesRxMongoSherlock {}