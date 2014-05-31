(ns bedquilt.core
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as time]
            [clj-time.coerce :refer [to-sql-time from-sql-time]]
            [bedquilt.admin :as admin]
            [bedquilt.util :as util]
            [bedquilt.db :as db]
            [cheshire.core :as json]))


;; public api
(declare get-db)
(declare find-one)
(declare save!)


(defn get-db [{:keys [db-host db-name user password]}]
  (db/pool {:subprotocol "postgresql"
            :subname (str "//" db-host "/" db-name)
            :user user
            :password password}))


(defn save!
  "Insert the supplied data into the specified collection"
  [db-spec collection data]
  (let [row (util/map->row data)]
    (do
      (admin/create-collection! db-spec collection)
      (if (db/document-exists? db-spec collection (:_id row))
        (db/update-document! db-spec collection row)
        (db/insert-document! db-spec collection row))
      (:_id row))))


(defn find-one
  "retreive a single document from the specified collection,
   whose id matches the one supplied. If the document does not exist then
   nil is returned instead"
  [db-spec collection id]
  (if (admin/collection-exists? db-spec collection)
    (let [db-row (first (jdbc/query
                         db-spec
                         [(str "select * from " (name collection) " "
                               "where _id = ?")
                          id]))]
      (if (not (nil? (:_id db-row)))
        (util/row->map db-row)
        nil))
    nil))


(defn delete!
  "Delete a single document from a collection,
   returns true if the deletion affected an existing document,
   or false if no documents were removed"
  [db-spec collection id]
  (if (admin/collection-exists? db-spec collection)
    (let [result (db/delete-document! db-spec collection id)]
      (= 1 (first result)))
    false))
