(ns bedquilt.db
  (:require [clojure.java.jdbc :as jdbc])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))


(declare pool)
(declare insert!)
(declare update!)
(declare doc-exists?)


(defn pool
  [spec]
  (let [cpds
        (doto (ComboPooledDataSource.)
          (.setDriverClass (:classname spec))
          (.setJdbcUrl (str "jdbc:"
                            (:subprotocol spec)
                            ":"
                            (:subname spec)))
          (.setUser (:user spec))
          (.setPassword (:password spec))
          ;; expire excess connections after 30 minutes of inactivity:
          (.setMaxIdleTimeExcessConnections (* 30 60))
          ;; expire connections after 3 hours of inactivity:
          (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))


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

