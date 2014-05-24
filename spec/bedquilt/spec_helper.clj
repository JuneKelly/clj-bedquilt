(ns bedquilt.spec-helper)


;; these env variables should be set for a test database
;; before running the test suite
(def db {:db-host  (System/getenv "DB_HOST")
         :db-name  (System/getenv "DB_NAME")
         :user     (System/getenv "DB_USER")
         :password (System/getenv "DB_PASSWORD")})
