package com.coditory.sherlock.samples.postgres.rxjava;

import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.rxjava.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.reactivex.rxjava3.core.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresRxAnnotatedMigrationSample {
    private static ConnectionFactory getConnectionFactory() {
        String database = "test";
        ConnectionFactoryOptions options = ConnectionFactoryOptions
            .parse("r2dbc:postgresql://localhost:5432/" + database)
            .mutate()
            .option(ConnectionFactoryOptions.USER, "postgres")
            .option(ConnectionFactoryOptions.PASSWORD, "postgres")
            .option(ConnectionFactoryOptions.DATABASE, database)
            .build();
        return ConnectionFactories.get(options);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), BindingMapper.POSTGRES_MAPPER);
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration())
            .migrate()
            .blockingGet();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration2())
            .migrate()
            .blockingGet();
    }

    public static class AnnotatedMigration {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public Completable changeSetA() {
            return Completable.fromRunnable(() -> logger.info("Annotated change-set: A"));
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public Completable changeSetB() {
            return Completable.fromRunnable(() -> logger.info("Annotated change-set: B"));
        }
    }

    public static class AnnotatedMigration2 {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public void changeSetA() {
            logger.info("Annotated change-set: A");
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public void changeSetB() {
            logger.info("Annotated change-set: B");
        }

        @ChangeSet(order = 2, id = "change-set-c")
        public void changeSetC() {
            logger.info("Annotated change-set: C");
        }
    }
}
