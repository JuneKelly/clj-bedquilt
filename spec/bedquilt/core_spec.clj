(ns bedquilt.core-spec
  (:require [bedquilt.core :as bq]
            [bedquilt.spec-helper :as h]
            [speclj.core :refer :all]))


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
