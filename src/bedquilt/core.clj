(ns bedquilt.core
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as time]
            [clj-time.coerce :refer [to-sql-time from-sql-time]]
            [cheshire.core :as json]))


(defn get-db [{:keys [db-host db-name user password]}]
  {:subprotocol "postgresql"
   :subname (str "//" db-host "/" db-name)
   :user user
   :password password})


(defn generate-id! []
  (str (java.util.UUID/randomUUID)))


(defn collection-exists? [db-spec collection-name]
  (let [result (jdbc/query db-spec
                           ["SELECT EXISTS(
                             SELECT * FROM information_schema.tables
                             WHERE table_schema = 'public' AND
                             table_name = ?);" collection-name])
        exists (= true (:exists (first result)))]
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


(defn has-id? [m]
  (contains? m :_id))


(defn map->row
  "convert map data into a list of items
   suitable for inserting into table,
   generating _id field if necessary"
  [m]
  (if (has-id? m)
    {:_id (:_id m)
     :data (-> m
               (dissoc :_id)
               json/generate-string)}
    {:_id (generate-id!)
     :data (json/generate-string m)}))


(defn row->map [row-data]
  (comment "TODO"))


(defn insert! [dbspec collection data]
  (let [row (map->row data)
        now (-> (time/now)
                to-sql-time)]
    (do
      (create-collection! dbspec collection)
      (jdbc/execute! dbspec
                     [(str "insert into " collection " (_id, data, _created) "
                           "values (cast(? as uuid), "
                           "cast(? as json),"
                           "?"
                           ");"
                           )
                      (:_id row)
                      (:data row)
                      now]))))


(defn delete! [dbspec collection id]
  (comment "TODO"))


(defn find-one [dbspec collection id]
  (comment "TODO"))

