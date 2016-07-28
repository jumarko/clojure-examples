(ns clojure-examples.clojure-core-guide
  "Namespace contains examples from clojure.core Guide: http://clojure-doc.org/articles/language/core_overview.html")

;;;
;;; Binding
;;;

;; let
(let [x 1 y 2]
  (println x y))

(let [x 1]
  (println x)
  (let [x 2]
    (println x)))


;; def
(def a "documentation" 0)


;; declare
(declare func<10 func<20)

(defn func<10 [x]
  (cond
    (< x 10) (func<10 (inc x))
    (< x 20) (func<20 x)
    :else "too far!"))

(defn func<20 [x]
  (cond
    (< x 10) (func<10 x)
    (< x 20) "More than 10, less than 20"
    :else "too far!"))

(func<10 8)


;; defn
(defn func "documentation!" [x] x)



;;;
;;; Branching
;;;

;; if
(if (< 10 9) "second" "third")
(if (seq '()) "second")


;; when
(when (< 10 11) (print "hey") 10)


;; for
(for [x [1 2 3] y [4 5 6]
      :when (and
             (even? x)
             (odd? y))]
  [x y]
  )


;; doseq
(doseq [x [1 2 3] y [4 5 6]]
  (println [x y]))



;;;
;;; Looping
;;;

;; recur
(defn factorial
  ([n]
   (factorial n 1))
  ([n acc]
   (if (zero? n)
     acc
     (recur (dec n) (* acc n)))))

(factorial 5)


;; loop
(defn factorial
  [n]
  (loop [n n
         acc 1]
    (if (zero? n)
      acc
      (recur (dec n) (* acc n)))))

(factorial 5)


;; trampoline
;; -> allows for mutual recursion without consuming stack space
(declare count-up1 count-up2)

(defn count-up1
  [result start total]
  (if (= start total)
    result
    #(count-up2 (conj result start) (inc start) total)))

(defn count-up2 [result start total]
  (if (= start total)
    result
    #(count-up1 (conj result start) (inc start) total)))

(trampoline count-up1 [] 0 10)


;; iterate
(take 10 (iterate inc 0))


;; reductions
(reductions + 10 [1 2 3])


;; map
(map vector [:a :b :c] [1 2 3] ["ah" "meh" "yuh"])
J


;;;
;;; Collection and Sequence Modification
;;;

;; assoc
(assoc {:a 1 :b 2} :a 4)
(assoc [10 20 30] 3 40) ;=> [10 20 30 40]
(assoc [10 20 30] 2 40) ;=> [10 20 40]

;; dissoc
(dissoc {:a 1 :b 2} :a)
;; doesn't work for vectors!
#_(dissoc [10 20 30] 2)


;; contains?
;; returns true if the provided _key_ is present in collection

(contains? {:a 1 :b 2 :c 3} :c)

;; for vectors it check if provided index exists, not value!
(contains? ["John" "Mary" "Paul"] "Paul") ;=> false
(contains? ["John" "Mary" "Paul"] 2) ;=> true

;; lists are not supported (wont' traverse a collection for a result
#_(contains? '(1 2 3) 0)


;; keep and remove
(keep #(if (odd? %) %) [1 2 3 4 5 6]) ;=> (1 3 5)

(remove odd? [1 2 3 4 5 6])


;; partition
(partition 3 (range 10))
(partition 3 2 (range 10))
(partition 3 2 [100 101] (range 13))

(partition-all 3 2 (range 13))


;;;
;;; Function Composition and Application
;;;

;; fnil
(defn say-info [name location hobby]
  (println name "is from" location "and enjoys" hobby))

(def say-info-patched (fnil say-info "Someone" "an unknown location" "Clojure"))

(say-info nil nil nil) ;=> nil is from nil and enjoys nil
(say-info-patched nil nil nil) ;=> Someone is from an unknown location and enjoys Clojure

(say-info "Robert" nil "giraffe migrations") ;=> Rober is from nil and enjoys giraffe migrations
(say-info-patched "Robert" nil "giraffe migrations") ;=> Rober is from unknown location and enjoys giraffe migrations


;; apply
(apply map + [[1 2 3] [1 2 3]])


;;;
;;; Associative collections
;;;

;; select-keys
(select-keys {:a 1 :b 2 :c 3} [:a :b]) ;=> {:a 1, :b 2}
