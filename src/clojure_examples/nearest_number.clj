;;; Given two numbers find the number which is nearest to 100.

(ns clojure-examples.nearest_number)

(defn diff-abs [num]
  (Math/abs (- 100 num)))

(defn nearestTo100 [num1, num2]
  (let [abs1 (diff-abs num1)
        abs2 (diff-abs num2)]
    (if (< abs1 abs2)
      num1
      num2)))

(nearestTo100 0 1)
(nearestTo100 0 -1)
(nearestTo100 100 101)
(nearestTo100 99 101)
(nearestTo100 -101 -100)
