package com.coditory.sherlock.infrastructure


import java.sql.DatabaseMetaData
import java.sql.ResultSet

import static com.coditory.sherlock.SqlInitializer.connection

class SqlIndexCreationSpec {
//  String collectionName = "other-locks"
//  MongoCollection<Document> collection = mongoClient.getDatabase(databaseName)
//    .getCollection(collectionName)
//  Sherlock locks = MongoSherlock.builder()
//    .withLocksCollection(collection)
//    .build()
//
//  @After
//  def removeCollection() {
//    collection.drop()
//  }
//
//  def "should create mongo indexes on initialize"() {
//    expect:
//      assertNoIndexes()
//    when:
//      locks.initialize()
//    then:
//      assertIndexesCreated()
//  }

//  def "should create mongo indexes on initialize"() {
//    expect:
//      assertNoIndexes()
//    when:
//      locks.initialize()
//    then:
//      assertIndexesCreated()
//  }

//  def "should create mongo indexes on first lock"() {
//    expect:
//      assertNoIndexes()
//    when:
//      locks.createLock("some-acquire")
//        .acquire()
//    then:
//      assertIndexesCreated()
//  }

//  private boolean assertNoIndexes() {
//    assertJsonEqual(getCollectionIndexes(), "[]")
//    return true
//  }
//extends Specification
//  private boolean assertIndexesCreated() {
//    assertJsonEqual(getCollectionIndexes(), """[
//        {"v": 2, "key": {"_id": 1, "acquiredBy": 1, "acquiredAt": 1}, "name": "_id_1_acquiredBy_1_acquiredAt_1", "ns": "$databaseName.$collectionName", "background": true},
//        {"v": 2, "key": {"_id": 1}, "name": "_id_", "ns": "$databaseName.$collectionName"}
//      ]""")
//    return true
//  }

  private String getTableIndexes() {
    List<String> schemaList = new ArrayList<String>();
    List<String> catalogList = new ArrayList<String>();
    List<String> indexs = new ArrayList<String>();
    String dbIndexName = null;
    ResultSet rs = null;
    System.out.println("Got Connection.");
    DatabaseMetaData metaData = connection.getMetaData();

    ResultSet schemas = metaData.getSchemas();
    ResultSet catalog = metaData.getCatalogs();
    while (schemas.next()) {
      String tableSchema = schemas.getString(1);
      schemaList.add(tableSchema);
    }
    while (catalog.next()) {
      String allCatalog = catalog.getString(1);
      catalogList.add(allCatalog);
    }


    for (int i = 0; i < schemaList.size(); i++) {
      try {
        if (schemaList.get(i) != null) {
          ResultSet indexValues = metaData.getIndexInfo(null, schemaList.get(i), "locks", true, false);

          while (indexValues.next()) {

            dbIndexName = indexValues.getString("INDEX_NAME");
            if (dbIndexName != null) {
              indexs.add(dbIndexName);
            }
          }
          System.out.println("CORRESPONDING TABLE SCHEMA IS : " + schemaList.get(i));
          System.out.println("INDEX_NAMES IS ::: " + indexs);
        }

      } catch (Exception e) {
      }
    }
  }

}
