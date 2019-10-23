(ns topn-github-repo.core
  (:require [clj-http.client :as client]
            [config.core :refer [env]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [peco.core :refer [tokenizer]])
  (:gen-class))

(def endpoint "https://api.github.com/")
(def list-type "issues")

(def url (str
           endpoint
           "repos/"
           (:git-owner env) "/"
           (:git-repo env) "/"
           list-type))

(def tokenize (tokenizer [:lower-case :remove-numbers :porter-stem :remove-stop-words]))

(defn tokenize-string [str-header]
  (map #(tokenize %) (str/split str-header #" ")))

(defn stem-data [list]
  (flatten (map #(tokenize-string %) list)))

(defn filter-columns
  [all-issues]
  (map #(:title %) all-issues))

(defn get-batch-of-issues
  ; limitation of 60 requests per hour: https://developer.github.com/v3/#rate-limiting
  [url num]
  (-> (client/get url {:as :json
                       :throw-entire-message? false
                       :query-params {"state" (:state env)
                                      "sort" (:sort-by env)
                                      "direction" (:sort-direction env)
                                      "page" (str num)
                                      "per_page" "20"
                                      }})
      :body
      (json/read-str :key-fn keyword)))

; hardcoded range of 50 with per_page 20 to complete 1000 issues could be done more elegantly
(defn get-all-issues []
  (flatten (map #(get-batch-of-issues url %) (range 50))))

(defn -main [& args]
  (println "Be aware! GitHub applies an API rate limitation on unauthenticated calls.")
  (->> (get-all-issues)
       filter-columns
       stem-data
       frequencies
       (sort-by second >)
       (take 10)))
