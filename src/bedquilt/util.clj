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
  (let [parsed-json (json/parse-string (str (:data row)))
        id (:_id row)]
    (if (not (nil? id))
      (-> parsed-json
          (assoc :_id id))
      nil)))
