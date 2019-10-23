(ns topn-github-repo.core
  (:require [clj-http.client :as client]
            [config.core :refer [env]]
            [clojure.data.json :as json])
  (:gen-class))

(def endpoint "https://api.github.com/")
(def url (str
           endpoint
           "repos/"
           (:git-owner env) "/"
           (:git-repo env) "/"
           "issues"))

(defn get-all-issues
  [url]
  (-> (client/get url {:as :json :query-params {"state" "all"
                                                "sort" "created"
                                                "direction" "desc"
                                                "page" "1"}})
      :body
      (json/read-str :key-fn keyword)
      first
      :title))

(defn -main [& args]
  (println url)
  (get-all-issues url))
