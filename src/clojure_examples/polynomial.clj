;; Example taken from http://java.ociweb.com/mark/clojure/article.html
;; Evaluataes a polynomial and its derivative.
(ns clojure-examples.polynomial)

(defn- get-exponents [coefs]
  (reverse (range (count coefs))))

(defn- polynomial
  "Computes the value of polynomial with the given coefficients for a given value x"
  [coefs x]
  (let [exponents (get-exponents coefs)]
    ;; Multiply each coefficient by x raised to the corresponding exponent and sum those results
    (apply + (map #(* %1 (Math/pow x %2))  coefs exponents))
    ))

;; Example: 3*4^2 + 2*4 + 1
(polynomial [3 2 1] 4)

(defn- derivative
  "Computes the derivative of given polynomial.
   Check get-exponents function as well."
  [coefs x]
  (let [exponents (get-exponents coefs)
        derivative-coefs (map #(* %1 %2) (butlast coefs) exponents)]
    (polynomial derivative-coefs x)))

(derivative [3 2 1] 4)

;;; alternative implementation of polynomial
(defn- polynomial2 [coefs x]
  (reduce #(+ (* x %1) %2) coefs))

(polynomial2 [3 2 1] 4)

(def f (partial polynomial [2 1 3])) ; 2x^2 + x + 3
(def f-prime (partial derivative [2 1 3])) ; 4x + 1

(println "f(2) =" (f 2)) ; -> 13.0
(println "f'(2) =" (f-prime 2)) ; -> 9.0

;; memoization
(def memo-f (memoize f))
(println "printing call")
(time (f 2))

(println "without ")
;; Note the use of an underscore for the binding that isn't used.
(dotimes [_ 3] (time (f 2)))

(println "with memoization")
(dotimes [_ 3] (time (memo-f 2)))
