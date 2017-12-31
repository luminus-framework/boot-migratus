(ns luminus.boot-migratus
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask]]
            [boot.util :as util]
            [migratus.core :as mcore]))

(defn migrate [config]
  "Bring up any migrations that are not completed."
  (println "migrating all outstanding migrations")
  (mcore/migrate config))

(defn up [config ids]
  "Bring up the migrations specified by their ids.  Skips any migrations
         that are already up."
  (println "migrating" ids)
  (apply mcore/up config ids))

(defn down [config ids]
  "Bring down the migrations specified by their ids.  Skips any migrations
         that are already down."
  (println "rolling back" ids)
  (apply mcore/down config ids))

(defn rollback [config]
  "Bring down the last applied migration."
  (println "rolling back last migration")
  (mcore/rollback config))

(defn pending [config]
  (println "listing pending migrations")
  (mcore/pending-list config))

(defn create [config name]
  "Create a new migration file with the current date and the given name."
  (mcore/create config name))

(defn reset [config]
  "Bring down all migrations, then bring them all back up."
  (mcore/reset config))

(deftask migratus
  "Maintain database migrations.

  Run migrations against a store. It is recommended to set the config via
  task-options!

  Possible values for command:
  migrate  Bring up any migrations that are not completed.
  rollback Bring down the last applied migration.
  up       Bring up the migrations specified by their ids.  Skips any
           migrations that are already up. Pass ids as a vector to :options.
  down     Bring down the migrations specified by their ids.  Skips any
           migrations that are already down. Pass ids as a vector to :options.
  create   Create a new migration file with the current date and the given
           name. Pass the name as a string to :options
  reset    Bring down all migrations, then bring them all back up.
  pending  Return a list of pending migrations.

  config should be either a map of config values or a function that takes no
  arguments and resolves to a map.

  If run without specifying a command, the migrate command will execute."
  [c command CMD str "The migratus command to run. Defaults to migrate."
   o options OPT edn "Options to be passed to the command. See specific command for details."
   m config CONFIG edn "The config map. See migratus documentation."]
  (boot/with-pass-thru _
    (let [con (if (map? config)
                config
                (config))]
    ;; TODO use clojure.spec to create a decent error message if isn't ifn or map.
      (case command
        "up" (up con options)
        "down" (down con options)
        "rollback" (rollback con)
        "pending" (pending con)
        "create" (create con options)
        "reset" (reset con)
        (migrate con)))))
