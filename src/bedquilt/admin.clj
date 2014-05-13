(ns bedquilt.admin
  (:require [clojure.java.jdbc :as jdbc]))


(defn collection-exists? [db-spec collection-name]
  (let [result (jdbc/query db-spec
                           ["SELECT EXISTS(
                             SELECT * FROM information_schema.tables
                             WHERE table_schema = 'public' AND
                             table_name = ?);" collection-name])
        exists (:exists (first result))]
    exists))


(defn create-collection! [db-spec collection-name]
  (if (not (collection-exists? db-spec collection-name))
    (do
      (jdbc/db-do-commands
       db-spec
       (jdbc/create-table-ddl collection-name
                             [:_id "uuid"]
                             [:data "json"]
                             [:_created "timestamp with time zone"]))
      true)
    false))
