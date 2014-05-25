(ns bedquilt.spec-helper
  (:require [clojure.java.jdbc :as jdbc]))


;; these env variables should be set for a test database
;; before running the test suite
(def db {:db-host  (System/getenv "DB_HOST")
         :db-name  (System/getenv "DB_NAME")
         :user     (System/getenv "DB_USER")
         :password (System/getenv "DB_PASSWORD")})


(defn cleanse-database!
  "drop everything"
  [db-spec]
  (do (jdbc/execute!
       db-spec
       ["drop schema public cascade; create schema public;"])))
