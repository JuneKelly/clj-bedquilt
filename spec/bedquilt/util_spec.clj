(ns bedquilt.util-spec
  (:require [speclj.core :refer :all]
            [bedquilt.util :as util]
            [bedquilt.spec-helper :as helper]))


(describe "random id"
  (it "should be 22 characters long string"
      (let [id (util/random-id!)]
        (should= 22 (count id))
        (should= java.lang.String (class id)))))
