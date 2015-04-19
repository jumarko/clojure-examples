(ns clojure-examples.palindrom-test
  (:require [clojure-examples.palindrom :refer :all]
            [clojure.test :refer :all]))

(deftest simple-palindrom
  (is (= (palindrom? "lol") true)))

(deftest simple-seq-palindrom
  (is (= (palindrom? [1 2 3 2 1]) true)))

(deftest not-a-palindrom
  (is (= (palindrom? "hello") false)))

(deftest generate-palindroms
  (is (= (gen-palindroms [:a :b :b]) '([:b :b] [:b :a :b]))))
