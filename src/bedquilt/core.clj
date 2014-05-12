(ns bedquilt.core
  (:require [clojure.java.jdbc :as sql]))


(defn get-db [{:keys [db-host db-name user password]}]
  {:subprotocol "postgresql"
   :subname (str "//" db-host "/" db-name)
   :user user
   :password password})


(defn generate-id! []
  (str (java.util.UUID/randomUUID)))


(defn collection-exists? [db-spec collection-name]
  (let [result (sql/query db-spec
                          [(str "SELECT EXISTS(
                                 SELECT * FROM information_schema.tables
                                 WHERE table_schema = 'public' AND
                                 table_name = '" collection-name "');")])
        exists (= true (:exists (first result)))]
    exists))


(defn create-collection! [db-spec collection-name]
  (if (not (collection-exists? db-spec collection-name))
    (do
      (sql/db-do-commands db-spec
                          (sql/create-table-ddl collection-name
                                                [:_id "varchar(32)"]
                                                [:data "json"]))
      true)
    false))



