(defproject topn-github-repo "0.1.0-SNAPSHOT"
  :description "A project that lists the most popular words from the most recent issues in a given github repo"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "3.10.0"]
                 [yogthos/config "1.1.6"]]
  :test-paths ["test"]
  :main topn-github-repo.core
  :jvm-opts ["-Dconfig=config/config.edn"]
  :profiles {:uberjar {:uberjar-name "topn-github-repo.jar"}}
  )
