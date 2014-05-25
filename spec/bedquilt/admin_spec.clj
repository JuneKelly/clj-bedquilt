(ns bedquilt.admin-spec
  (:require [bedquilt.admin :as admin]
            [bedquilt.core :as bq]
            [bedquilt.spec-helper :as h]
            [speclj.core :refer :all]))


(def db (bq/get-db h/db))


(def test-coll :test)


(describe "create-collection! and collection-exists?"

  (before (do (h/cleanse-database! db)))

  (it "should create a collection table"
      (do
        (should= false (admin/collection-exists? db test-coll))
        (admin/create-collection! db test-coll)
        (should= true (admin/collection-exists? db test-coll))))

  (it "should return true when creating a new collection"
      (should= true (admin/create-collection! db test-coll)))

  (it "should return false when collection already exists"
      (do
        (admin/create-collection! db test-coll)
        (should= false (admin/create-collection! db test-coll)))))


(describe "drop-collection!"

  (before (do (h/cleanse-database! db)))

  (it "should remove a collection"
      (do
        (should= false (admin/collection-exists? db test-coll))
        (admin/create-collection! db test-coll)
        (should= true (admin/collection-exists? db test-coll))
        (admin/drop-collection! db test-coll)
        (should= false (admin/collection-exists? db test-coll))))

  (it "should return false when the dropped collection did not exist"
      (should= false (admin/drop-collection! db test-coll)))

  (it "should return true when the dropped collection did exist"
      (do
        (admin/create-collection! db test-coll)
        (should= true (admin/drop-collection! db test-coll)))))
