(ns bedquilt.transformations-spec
  (:require [speclj.core :refer :all]
            [bedquilt.util :as util]))


(describe "random id"
  (it "should be 22 characters long string"
    (let [id (util/random-id!)]
      (should= 22 (count id))
      (should= java.lang.String (class id)))))
