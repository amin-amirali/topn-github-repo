(ns topn-github-repo.core
  (:require [clj-http.client :as client]
            [config.core :refer [env]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [peco.core :refer [tokenizer]]
            [clojure.tools.logging :as log])
  (:use clojure.pprint)
  (:gen-class))

(def endpoint "https://api.github.com/")
(def list-type "issues")

(def tokenize (tokenizer [:lower-case :remove-numbers :porter-stem :remove-stop-words]))

(defn tokenize-string [str-header]
  (map #(tokenize %) (str/split str-header #" ")))

(defn stem-data [list]
  (flatten (map #(tokenize-string %) list)))

(defn select-data-column
  [all-issues]
  (map #((:data-field env) %) all-issues))

(defn do-api-call
  [url num]
  (let [response (client/get url {:as :json
                                  :throw-exceptions false
                                  :query-params {"state" (:state env)
                                                 "sort" (:sort-by env)
                                                 "direction" (:sort-direction env)
                                                 "page" (str num)
                                                 "per_page" "20"}})]
    (case (:status response)
      200 response
      403 (log/error (str "403 response received from server. This tipically means that the API calls limit has been "
                          "exceeded. Try again later on. See the following link for more details: "
                          "https://developer.github.com/v3/#rate-limiting"))
      (throw (Exception. response)))))

(defn get-batch-of-issues
  [url num]
  (-> (do-api-call url num)
      :body
      (json/read-str :key-fn keyword)))

; hardcoded range of 50 with per_page 20 to complete 1000 issues could be done more elegantly
(defn get-all-issues [url]
  (flatten (map #(get-batch-of-issues url %) (range 50))))

(defn -main [& args]
  (let [url (str
              endpoint
              "repos/"
              (:git-owner env) "/"
              (:git-repo env) "/"
              list-type)
        results (->> (get-all-issues url)
                     select-data-column
                     stem-data
                     frequencies
                     (sort-by second >)
                     (take (:top-n-words env)))]
    (pprint results)))
