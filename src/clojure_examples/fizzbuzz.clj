(ns clojure-examples.fizzbuzz)

(for [x (range 1 100)]
  (cond
   (zero? (mod x 15)) "FizzBuzz"
   (zero? (mod x 5)) "Buzz"
   (zero? (mod x 3)) "Fizz"
   :default x
   ))
