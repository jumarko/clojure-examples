(ns clojure-examples.print-java)

(defn print-java [string]
  (loop [head  [(first string)]
        tail (rest string)]
    (when (seq tail)
      (println head)
      (recur (conj head (first tail)) (rest tail))
      )))

(print-java "JAVAJ2EE")
