(ns bedquilt.db
  (:require [clojure.java.jdbc :as jdbc]))


(declare insert!)
(declare update!)
(declare doc-exists?)


(defn insert-document! [db-spec collection row]
  (jdbc/execute! db-spec
                 [(str "insert into " (name collection) " (_id, data) "
                       "values (?, cast(? as json));")
                  (:_id row)
                  (:data row)]))


(defn update-document! [db-spec collection row]
  (jdbc/execute! db-spec
                 [(str "UPDATE " (name collection)
                       " SET data = cast(? as json) "
                       "WHERE _id = ?")
                  (:data row)
                  (:_id row)]))


(defn document-exists? [db-spec collection id]
  (let [result (jdbc/query db-spec
                           [(str "SELECT EXISTS("
                                 "SELECT _id from " (name collection)
                                 " WHERE _id = ?);")
                            id])]
    (:exists (first result))))


(defn delete-document! [db-spec collection id]
  (jdbc/execute! db-spec
                 [(str "DELETE FROM " (name collection)
                       " WHERE _id = ?")
                  id]))

