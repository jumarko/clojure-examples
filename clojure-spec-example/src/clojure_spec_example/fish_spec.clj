(ns clojure-spec-example.fish-spec
  "Examples for blog post about Clojure.spec from Carin Meier: http://gigasquidsoftware.com/blog/2016/05/29/one-fish-spec-fish/"
  (:require [clojure.spec :as s]))

(def fish-numbers {0 "Zero"
                   1 "One"
                   2 "Two"})

;; Let's define a specification for all valid fish numbers
(s/def ::fish-number (set (keys fish-numbers)))

(s/valid? ::fish-number 1)
(s/valid? ::fish-number 5)
(s/explain ::fish-number 5)

;; Let's defines another specification for all valid fish colors
(s/def ::color #{"Red" "Blue" "Dun"})


;; Now we can start specifying things about the sequence of values in parameter vector
(s/def ::first-line (s/cat :n1 ::fish-number :n2 ::fish-number :c1 ::color :c2 ::color))
(s/explain ::first-line [1 2 "Red" "Black"])

;; We can do better - e.g. the second number should be one bigger than the first number
(defn one-bigger? [{:keys [n1 n2]}]
  (= n2 (inc n1)))

;; Also the colors should not be the samve value
(s/def ::first-line (s/and (s/cat :n1 ::fish-number :n2 ::fish-number :c1 ::color :c2 ::color)
                           one-bigger?
                           #(not= (:c1 %) (:c2 %))))

(s/valid? ::first-line [1 2 "Red" "Blue"])
(s/conform ::first-line [1 2 "Red" "Blue"])

(s/valid? ::first-line [2 1 "Red" "Blue"])
(s/explain ::first-line [2 1 "Red" "Blue"])



;;; Generating test data - and poetry with specification
(s/exercise ::first-line 5)

;; Let's add one essential ingredient - rhyming!
(defn fish-number-rhymes-with-color? [{n :n2 c :c2}]
  (or
   (= [n c] [2 "Blue"])
   (= [n c] [1 "Dun"])))

(s/def ::first-line (s/and (s/cat :n1 ::fish-number :n2 ::fish-number :c1 ::color :c2 ::color)
                           one-bigger?
                           #(not= (:c1 %) (:c2 %))
                           fish-number-rhymes-with-color?))

(s/valid? ::first-line [1 2 "Red" "Blue"])
(s/explain ::first-line [1 2 "Red" "Dun"])

(s/exercise ::first-line)



;;; Using spec with functions
(defn fish-line [n1 n2 c1 c2]
  (clojure.string/join " "
                       (map #(str % " fish.")
                            [(get fish-numbers n1)
                             (get fish-numbers n2)
                             c1
                             c2])))
(s/fdef fish-line
        :args ::first-line
        :ret string?)
(s/instrument #'fish-line)

(fish-line 1 2 "Red" "Blue")
(fish-line 2 1 "Red" "Blue")
