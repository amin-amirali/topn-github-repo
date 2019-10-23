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

(defn get-all-issues
  [url]
  (-> (client/get url {:as :json :query-params {"state" (:state env)
                                                "sort" (:sort-by env)
                                                "direction" (:sort-direction env)
                                                ;"page" "10000"
                                                ;"per_page" "1"
                                                }})
      :body
      (json/read-str :key-fn keyword)))

(defn -main [& args]
  (->> (get-all-issues url)
       filter-columns
       stem-data
       frequencies
       (sort-by second >)
       (take 10)))
