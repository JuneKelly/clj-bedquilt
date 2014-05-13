(ns bedquilt.util
  (:require [cheshire.core :as json]))


(defn json-coerce
  "Behold, a Ghastly hack!
   convert data to json string and back again,
   useful for ensuring datetime fields have been converted
   to strings before attempting comparisons."
  [data]
  (-> data
      (json/generate-string)
      (json/parse-string true)))
