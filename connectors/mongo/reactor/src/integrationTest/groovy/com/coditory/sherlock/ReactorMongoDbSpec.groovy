package com.coditory.sherlock

class ReactorMongoIndexCreationSpec extends MongoIndexCreationSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoLockStorageSpec extends MongoLockStorageSpec
        implements UsesReactorMongoSherlock {}