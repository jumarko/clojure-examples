(ns clojure-examples.palindrom
  (:require [clojure.math.combinatorics :as comb]))

;; Palindrom is a string that "reads" the same forwards and backwards
(defn palindrom? [str]
  (= (seq str) (reverse str)))

(defn gen-palindroms [col]
  (->> (comb/partitions col) ;; calculate groupings
       (apply concat)        ;; gather them up
       (filter #(> (count %) 1)) ;; remove singletons
       set                   ;; remove duplicates
       (map comb/permutations) ;; calculate arrangements
       (apply concat)        ;; gather them up
       (filter palindrom?)
       ) )
