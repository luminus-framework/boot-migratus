# boot-migratus

MIGRATE ALL THE THINGS!

A boot plugin for the [Migratus](https://github.com/yogthos/migratus) library.

[![Clojars Project](https://img.shields.io/clojars/v/luminus/boot-migratus.svg)](https://clojars.org/luminus/boot-migratus)

A general migration framework, with an implementation for database migrations.

Designed to be compatible with a git based work flow where multiple topic branches may exist simultaneously, and be merged into a master branch in unpredictable order.

This is accomplished two ways:

1. Migration ids are not assumed to be incremented integers. It is recommended that they be timestamps (e.g. ‘20111202091200’).
2. Migrations are considered for completion independently.

In contrast, using a single global version for a store and incremented integers for migration versions, it is possible for a higher numbered migration to get merged to master and deployed before a lower numbered migration, in which case the lower numbered migration would never get run, unless it is renumbered.

Migratus does not use a single global version for a store. It considers each migration independently, and runs all uncompleted migrations in sorted order.

## Usage

- Add Migratus as a dependency and this plugin:

```clojure
(set-env! :dependencies '[[migratus "1.0.3"]
                          [luminus/boot-migratus "1.0" :scope "test"]])
                          
(require '[luminus.boot-migratus :refer [migratus]])
```

- Specify the config as via `task-options!`:

```
(task-options!
  migratus {:store :database
            :migration-dir "migrations"
            :db {:classname "com.mysql.jdbc.Driver"
                :subprotocol "mysql"
                :subname "//localhost/migratus"
                :user "root"
                :password ""}}})
```

- Add the following code to “src/migrations/20111206154000-create-foo-table.up.sql”

```
CREATE TABLE IF NOT EXISTS foo(id BIGINT);
```
    
- Run `boot migratus -c migrate`

# Usage

   Migratus can be used programmatically by calling one of the following
   functions:

   | Function                   | Description                                                                               |
   |----------------------------|-------------------------------------------------------------------------------------------|
   | migratus.core/create       | Run 'init' to initialize the database, e.g: create a schema.                              |
   | migratus.core/create       | Run 'create' to generate migration files.                                                 |
   | migratus.core/migrate      | Run 'migrate' for any migrations that have not been run.                                  |
   | migratus.core/rollback     | Run 'rollback' to revert last successful migration.                                       |
   | migratus.core/up           | Run 'up' for the specified migration ids. Will skip any migration that is already up.     |
   | migratus.core/down         | Run 'down' for the specified migration ids. Will skip any migration that is already down. |
   | migratus.core/reset        | Run 'down' for all migrations that have been run, and 'up' for all migrations.            |
   | migratus.core/pending-list | Run 'pending-list' to list pending migrations                                             |

   The 'create' command will generate the migration files with the supplied name. The files will be placed in the migrations
   directory. Each file will be prefixed with the current timestamp and the up migration file will be postfixed with '.up.sql',
   while the down migration file will be postfixed with '.down.sql', e.g:
   
   `boot migratus -c create -o "add-users-table"`
   
   will generate the following files:
   
   ```
   20160303102023-add-users-table.down.sql
   20160303102023-add-users-table.up.sql
   ```

   See the docstrings of each function for more details.

## License

Copyright © 2017 Daniel Manila

Distributed under the [MIT License](https://opensource.org/licenses/MIT).
