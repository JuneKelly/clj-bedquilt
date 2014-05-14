(ns bedquilt.db
  (:require [clojure.java.jdbc :as jdbc]))


(declare insert!)
(declare update!)
(declare doc-exists?)


(defn insert! [dbspec collection row]
  (jdbc/execute! dbspec
                 [(str "insert into " (name collection) " (_id, data) "
                       "values (?, cast(? as json));")
                  (:_id row)
                  (:data row)]))


(defn update! [dbspec collection row]
  (jdbc/execute! dbspec
                 [(str "UPDATE " (name collection)
                       " SET data = cast(? as json) "
                       "WHERE _id = ?")
                  (:data row)
                  (:_id row)]))


(defn doc-exists? [dbspec collection id]
  (let [result (jdbc/query dbspec
                           [(str "SELECT EXISTS("
                                 "SELECT _id from " (name collection)
                                 " WHERE _id = ?);")
                            id])]
    (:exists (first result))))
