(ns bedquilt.util-spec
  (:require [speclj.core :refer :all]
            [bedquilt.util :as util]
            [bedquilt.spec-helper :as helper]
            [cheshire.core :as json]))


(describe "random id"
  (it "should be 22 characters long string"
      (let [id (util/random-id!)]
        (should= 22 (count id))
        (should= java.lang.String (class id)))))


(describe "row->map"

  (it "should return a map with keys :_id and :_data"
      (let [m {:_id "lol" :a 1 :b 2}
            r (util/map->row m)]
        (should== [:_id :data] (keys r))
        (should== {"a" 1 "b" 2} (json/parse-string (:data r)))))

  (it "should keep the :_id field if present"
      (let [m {:_id "asdf" :a 1}
            r (util/map->row m)]
        (should= (:_id m)
                 (:_id r))))

  (it "should generate a new :_id if not present"
      (let [m {:a 1}
            r (util/map->row m)]
        (should-not-contain :id m)
        (should-contain :_id r)))

  (it "should put all fields except :_id in the :data field as json string"
      (let [m {:_id "asdf" :a 1 :b 2 :c [3 4]}
            r (util/map->row m)
            data (json/parse-string (:data r))]
        (should== {"a" 1
                   "b" 2
                   "c" [3 4]}
                  data)
        (should-not-contain "_id" data))))
