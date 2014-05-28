(defproject bedquilt "0.2.0"

  :description "A JSON document store on PostgreSQL"

  :url "https://github.com/ShaneKilkelly/bedquilt"

  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [cheshire "5.3.1"]
                 [clj-time "0.7.0"]
                 [crypto-random "1.2.0"]
                 [postgresql/postgresql "8.4-702.jdbc4"]]

  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}

  :plugins [[speclj "2.5.0"]]

  :test-paths ["spec"])
