(ns bedquilt.core
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as time]
            [clj-time.coerce :refer [to-sql-time from-sql-time]]
            [bedquilt.admin :as admin]
            [bedquilt.util :as util]
            [cheshire.core :as json]))


(defn get-db [{:keys [db-host db-name user password]}]
  {:subprotocol "postgresql"
   :subname (str "//" db-host "/" db-name)
   :user user
   :password password})


(defn generate-id! []
  (str (java.util.UUID/randomUUID)))


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
    {:_id (util/random-id!)
     :data (json/generate-string m)}))


(defn row->map
  "Transform a database row into a clojure map, resembling json."
  [row]
  (let [parsed-json (json/parse-string (str (:data row)))
        id (:_id row)
        created (:_created row)]
    (if (not (nil? id))
      (-> parsed-json
          (assoc :_id id)
          (assoc :_meta {:created created})
          util/json-coerce)
      nil)))


(defn insert!
  "Insert the supplied data into the specified collection"
  [dbspec collection data]
  (let [row (map->row data)
        now (-> (time/now)
                to-sql-time)]
    (do
      (admin/create-collection! dbspec collection)
      (jdbc/execute! dbspec
                     [(str "insert into " collection " (_id, data, _created) "
                           "values (?, cast(? as json), ?);")
                      (:_id row)
                      (:data row)
                      now])
      (:_id row))))


(defn delete! [dbspec collection id]
  (comment "TODO"))


(defn find-one
  "retreive a single document from the specified collection,
   whose id matches the one supplied. If the document does not exist then
   nil is returned instead"
  [dbspec collection id]
  (let [db-row (first (jdbc/query
                       dbspec
                       [(str "select * from " collection " "
                             "where _id = cast(? as uuid)")
                        id]))]
    (if (not (nil? (:_id db-row)))
      (row->map db-row)
      nil)))

