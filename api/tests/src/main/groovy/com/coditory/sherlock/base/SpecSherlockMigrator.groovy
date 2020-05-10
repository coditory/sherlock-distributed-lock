package com.coditory.sherlock.base

interface SpecSherlockMigrator {
  String getMigrationId();

  SpecSherlockMigrator addChangeSet(String changeSetId, Runnable changeSet);

  SpecSherlockMigrator addAnnotatedChangeSets(Object object);

  List<String> migrate();
}
