(ns bedquilt.core-spec
  (:require [bedquilt.core :as bq]
            [bedquilt.spec-helper :as h]
            [speclj.core :refer :all]))


(def db (bq/get-db h/db))


(describe "get-db function"

  (it "should take a map and return a map suitable for use with jdbc"
      (let [db (bq/get-db {:db-host "localhost"
                           :db-name "something"
                           :user "user"
                           :password "password"})]
        (should (map? db))
        (should== [:subprotocol :subname :user :password]
                  (keys db))
        (doseq [[_ v] db]
          (should (string? v))))))


(describe "save!"

  (before (do (h/cleanse-database! db)))

  (it "should insert a new document in database"
      (let [doc {:_id "asdf" :a 1 :b 2}
            id (bq/save! db :test doc)]
        (should= (:_id doc) id)
        (should-not= nil (bq/find-one db :test id))))

  (it "should generate an :_id field if none is supplied"
      (let [doc {:a 1 :b 2}
            id (bq/save! db :test doc)]
        (should-not= nil id)
        (should (string? id))
        (should= 22 (count id))
        (should-contain :_id (bq/find-one db :test id)))))


(describe "find-one"

  (before (do (h/cleanse-database! db)))

  (it "should retrieve a saved document from a collection"
      (let [original-doc {:_id "asdf" :lol "wtf"}
            id (bq/save! db :test original-doc)
            found-doc (bq/find-one db :test id)]
        (should== found-doc original-doc)))

  (it "should return nil when neither document nor collection exist"
      (let [result (bq/find-one db :test "doesntexist")]
        (should= nil result)))

  (it "should return nil when document is not in collection which does exist"
      (let [id (bq/save! db :test {:_id "yesexists" :a 1})
            result (bq/find-one db :test "doesntexist")]
        (should= nil result))))


(describe "delete!"

  (before (do (h/cleanse-database! db)))

  (it "should delete an existing document"
      (let [original-doc {:_id "asdf" :lol "wtf"}
            id (bq/save! db :test original-doc)]
        (should-not= nil (bq/find-one db :test id))
        (should= true (bq/delete! db :test id))
        (should= nil (bq/find-one db :test id))))

  (it "should return false if the document does not exist"
      (let [original-doc {:_id "asdf" :lol "wtf"}
            id (bq/save! db :test original-doc)]
        (should-not= nil (bq/find-one db :test id))
        (should= false (bq/delete! db :test "someotherid"))))

  (it "should return false if the collection doesn't exist anyway"
      (should= false (bq/delete! db :test "asdf"))))
