(ns clojure-examples.better-living-through-clojure)

;;; Examples from presentation "Better Living Through Clojure":
;;; https://docs.google.com/presentation/d/1y8TJECz9b1n_gTgeL2qdXWXThkiPnZ1gXGozfkPsWcY/edit#slide=id.p


;; slide 6: Hello World
(println "Hello World")


;; slide 9: define a function and call it
(def myfun (fn [a b] (+ (* 2 a) b)))
(myfun 2 3)

(def myfun #(+ (* 2 %1) %2))
(myfun 2 3)

(defn myfun [a b] (+ (* 2 a) b))
(myfun 2 3)


;; slide 10: Data Literals
42 ; Long
12345678912345678912 ; BigInteger
1.234 ; Double
1.123M ; BigDecimal
22/7 ; Ratio
"fred" ; String
\a ; Character
:foo ; Keyword
'fred ; Symbol
true ; Boolean
nil ; nil
#"a*b" ; Regex


;; slide 11: Collection types

;; lists
(list 1 2 3)
'(1 2 3)

;; vectors
[1 2 :a :b]

;; maps
{:a 1 :b 2}

;; sets
#{:a :b :c}


;; slide 13: Working with lists
(+ 1 2 3)
(first [1 2 3])
(rest [1 2 3])
(cons "x" [1 2 3])
(take 2 [1 2 3 4 5])
(drop 2 [1 2 3 4 5])
(range 10)
(filter odd? (range 10))
(map odd? (range 10))
(reduce + (range 10))
(take 9 (cycle [1 2 3 4]))
(interleave [:a :b :c :d :e] [1 2 3 4 5])
(partition 3 [1 2 3 4 5 6 7 8 9])
(map vector [:a :b :c :d :e] [1 2 3 4 5])
(interpose \| "asdf")
(apply str (interpose \| "asdf"))


;; slide 15: Working With Maps and Sets
(def m {:a 1 :b 2 :c 3})

(m :b)
(:b m)
(keys m)
(assoc m :d 4 :c 42)
(merge-with + m {:a 2 :b 3})
(clojure.set/union #{:a :b :c} #{:c :d :e})
(clojure.set/join
  #{ {:a 1 :b 2 :c 3} {:a 1 :b 21 :c 42}}
  #{ {:a 1 :b 2 :e 5} {:a 1 :b 21 :d 4}})


;; slide 16 and next: Fibonacci
(defn next-fib [[x y]]
  [y (+ x y)])

(next-fib [3 5])
(take 10 (iterate next-fib [0 1]))
(nth (iterate next-fib [0 1]) 10)

(defn nth-fib [n]
  (first (nth (iterate next-fib [0 1]) n)))

(nth-fib 10)

(nth-fib 100);=> integer overflow; hint +' supports arbitrary precision

(defn next-fib [[x y]]
  [y (+' x y)])
(nth-fib 100)

;; memoization - cache results for the same arguments
(def mem-nth-fib (memoize nth-fib))

(time (nth-fib 100000))
;; make sure to do multiple invocations!
(time (mem-nth-fib 100000))


;; slide 27: Simple Data Manipulation
;;
;; 3 Challenges:
;; - find the first article in the collection that has tag java
;; - group all the articles based on author
;; - find all the different tags used in the collections

(def articles
  [{:title "3 Langs" :author "boris" :tags #{:java :clojure :haskell}}
   {:title "mylangs" :author "rocky" :tags #{:ruby :clojure :haskell}}
   {:title "2 Langs" :author "boris" :tags #{:java :haskell}}])

;; Find the first article in the collection that has tag java
(first
  (filter (fn [article] (get-in article [:tags :java])) articles))

;; Group all the articles based on author
(clojure.pprint/pprint
  (group-by :author articles))

;; Find all the different tags used in the collections
(set (mapcat :tags articles))
;; or
(apply clojure.set/union (map :tags articles))


;;; Slide 32: UFO Data Graphing Exercise
;;; create new project
;;;   lein new app ufos
;;; add dependencies
;;;   [enlive "1.1.1"]
;;;   [incanter "1.5.6"]







