(ns bedquilt.core
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as time]
            [clj-time.coerce :refer [to-sql-time from-sql-time]]
            [bedquilt.admin :as admin]
            [bedquilt.util :as util]
            [bedquilt.db :as db]
            [cheshire.core :as json]))


(declare get-db)
(declare find-one)
(declare save!)

(declare map->row)
(declare row-map)


(defn get-db [{:keys [db-host db-name user password]}]
  {:subprotocol "postgresql"
   :subname (str "//" db-host "/" db-name)
   :user user
   :password password})


(defn map->row
  "convert map data into a list of items
   suitable for inserting into table,
   generating _id field if necessary"
  [m]
  (if (contains? m :_id)
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
        id (:_id row)]
    (if (not (nil? id))
      (-> parsed-json
          (assoc :_id id)
          util/json-coerce)
      nil)))


(defn save!
  "Insert the supplied data into the specified collection"
  [dbspec collection data]
  (let [row (map->row data)]
    (do
      (admin/create-collection! dbspec collection)
      (if (db/doc-exists? dbspec collection (:_id row))
        (db/update! dbspec collection row)
        (db/insert! dbspec collection row))
      (:_id row))))


(defn find-one
  "retreive a single document from the specified collection,
   whose id matches the one supplied. If the document does not exist then
   nil is returned instead"
  [dbspec collection id]
  (let [db-row (first (jdbc/query
                       dbspec
                       [(str "select * from " collection " "
                             "where _id = ?")
                        id]))]
    (if (not (nil? (:_id db-row)))
      (row->map db-row)
      nil)))


(defn delete! [dbspec collection id]
  (comment "TODO"))
