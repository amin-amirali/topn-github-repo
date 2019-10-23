(ns topn-github-repo.core-test
  (:require [clojure.test :refer :all]
            [topn-github-repo.core :refer :all]))

(def some-response
  [{:foo1 "bar1" :title "hello world one"}
   {:foo1 "bar2" :title "hello world two"}
   {:foo1 "bar3" :title "hello world three"}])

(deftest a-test
  (testing "Testing function: select-data-column"
    (is (= (select-data-column some-response)
           ["hello world one"
            "hello world two"
            "hello world three"])))
  (testing "Tokenizing"
    (is (= (tokenize-string (:title (first some-response)))
           ;last field is empty since we are removing numbers from stemming
           '(["hello"] ["world"] [])))))
