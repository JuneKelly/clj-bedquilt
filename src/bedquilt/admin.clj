(ns bedquilt.admin
  (:require [clojure.java.jdbc :as jdbc]))


(defn collection-exists? [db-spec collection]
  (let [result (jdbc/query db-spec
                           ["SELECT EXISTS(
                             SELECT * FROM information_schema.tables
                             WHERE table_schema = 'public' AND
                             table_name = ?);" (name collection)])
        exists (:exists (first result))]
    exists))


(defn create-collection! [db-spec collection]
  (if (not (collection-exists? db-spec collection))
    (do
      (jdbc/db-do-commands
       db-spec
       (jdbc/create-table-ddl (name collection)
                             [:_id "varchar(128)" :primary :key]
                             [:data "json" :not :null]))
      true)
    false))


(defn drop-collection! [db-spec collection]
  (if (collection-exists? db-spec collection)
    (do
      (jdbc/db-do-commands
       db-spec
       (jdbc/drop-table-ddl (name collection)))
      true)
    false))
