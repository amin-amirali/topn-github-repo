(ns topn-github-repo.core-test
  (:require [clojure.test :refer :all]
            [config.core :refer [env]]
            [topn-github-repo.core :refer :all]))

(def data-field (:data-field env))

(def some-response
  [{:foo1 "bar1" data-field "hello world one"}
   {:foo1 "bar2" data-field "hello world two"}
   {:foo1 "bar3" data-field "hello world three"}])

(deftest a-test
  (testing "Testing function: select-data-column"
    (is (= (select-data-column some-response)
           ["hello world one"
            "hello world two"
            "hello world three"])))
  (testing "Tokenizing"
    (is (= (tokenize-string (data-field (first some-response)))
           ;last field is empty since we are removing numbers from stemming
           '(["hello"] ["world"] [])))))
