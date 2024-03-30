package com.coditory.sherlock.samples.mysql.reactor;

import com.coditory.sherlock.migrator.ChangeSet;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.migrator.SherlockMigrator;
import com.coditory.sherlock.sql.BindingMapper;
import com.coditory.sherlock.sql.reactor.SqlSherlock;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class MySqlReactorAnnotatedMigrationSample {
    private static ConnectionFactory getConnectionFactory() {
        String database = "test";
        ConnectionFactoryOptions options = ConnectionFactoryOptions
            .parse("r2dbc:mysql://localhost:3306/" + database)
            .mutate()
            .option(ConnectionFactoryOptions.USER, "mysql")
            .option(ConnectionFactoryOptions.PASSWORD, "mysql")
            .option(ConnectionFactoryOptions.DATABASE, database)
            .build();
        return ConnectionFactories.get(options);
    }

    public static void main(String[] args) {
        Sherlock sherlock = SqlSherlock.create(getConnectionFactory(), BindingMapper.MYSQL_MAPPER);
        // first commit - all migrations are executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration())
            .migrate()
            .block();
        // second commit - only new change-set is executed
        SherlockMigrator.builder(sherlock)
            .addAnnotatedChangeSets(new AnnotatedMigration2())
            .migrate()
            .block();
    }

    public static class AnnotatedMigration {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @ChangeSet(order = 0, id = "change-set-a")
        public Mono<?> changeSetA() {
            return Mono.fromRunnable(() -> logger.info("Annotated change-set: A"));
        }

        @ChangeSet(order = 1, id = "change-set-b")
        public Mono<?> changeSetB() {
            return Mono.fromRunnable(() -> logger.info("Annotated change-set: B"));
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
