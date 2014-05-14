(ns bedquilt.core
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as time]
            [clj-time.coerce :refer [to-sql-time from-sql-time]]
            [bedquilt.admin :as admin]
            [bedquilt.util :as util]
            [cheshire.core :as json]))

(declare get-db)
(declare generate-id)
(declare has-id?)
(declare map->row)
(declare row-map)
(declare insert!)
(declare update!)
(declare save!)
(declare find-one)
(declare doc-exists?)


(defn get-db [{:keys [db-host db-name user password]}]
  {:subprotocol "postgresql"
   :subname (str "//" db-host "/" db-name)
   :user user
   :password password})


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
        id (:_id row)]
    (if (not (nil? id))
      (-> parsed-json
          (assoc :_id id)
          util/json-coerce)
      nil)))


(defn- insert! [dbspec collection row]
  (jdbc/execute! dbspec
                 [(str "insert into " collection " (_id, data) "
                       "values (?, cast(? as json));")
                  (:_id row)
                  (:data row)]))


(defn- update! [dbspec collection row]
  (jdbc/execute! dbspec
                 [(str "UPDATE " collection " SET data = cast(? as json) "
                       "WHERE _id = ?")
                  (:data row)
                  (:_id row)]))

(defn save!
  "Insert the supplied data into the specified collection"
  [dbspec collection data]
  (let [row (map->row data)]
    (do
      (admin/create-collection! dbspec collection)
      (if (doc-exists? dbspec collection (:_id row))
        (update! dbspec collection row)
        (insert! dbspec collection row))
      (:_id row))))


(defn delete! [dbspec collection id]
  (comment "TODO"))


(defn doc-exists? [dbspec collection id]
  (let [result (jdbc/query dbspec
                           [(str "SELECT EXISTS("
                                 "SELECT _id from " collection " "
                                 "WHERE _id = ?);")
                            id])]
    (:exists (first result))))


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

