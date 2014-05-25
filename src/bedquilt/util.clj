(ns bedquilt.util
  (:require [cheshire.core :as json]
            [crypto.random :as random]))


(defn random-id! []
  (random/url-part 16))


(defn map->row
  "convert map data into a list of items
   suitable for inserting into table,
   generating _id field if necessary"
  [m]
  {:_id (or (:_id m) (random-id!))
   :data (-> m
             (dissoc :_id)
             json/generate-string)})


(defn row->map
  "Transform a database row into a clojure map, resembling json."
  [row]
  (let [parsed-json (json/parse-string (str (:data row)) true)
        id (:_id row)]
    (if (not (nil? id))
      (-> parsed-json
          (assoc :_id id))
      nil)))


(defn db-sql
  "A helper which generates sql for creating a new bedquilt database"
  [db-name, user]
  (format
   "CREATE DATABASE %s
    WITH OWNER = %s
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    CONNECTION LIMIT = -1
    TEMPLATE template0;"
   (name db-name)
   (name user)))
