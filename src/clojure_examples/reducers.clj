(ns clojure-examples.reducers
  (:require [clojure.core.reducers :as r]))

;;; Examples for the article Improving your Clojure code with core.reducers: https://adambard.com/blog/clojure-reducers-for-mortals/

(defn benchmark [f N times]
  (let [nums (vec (range N))
        start (java.lang.System/currentTimeMillis)]
    (dotimes [n times]
      (f nums))
    (- (java.lang.System/currentTimeMillis) start)))

(defn eager-map "A dumb map" [& args]
  (doall (apply map args)))

(defn eager-filter "An eager filter" [& args]  
  (doall (apply filter args)))

(defn eager-test [nums]
  (eager-filter even? (eager-map inc nums)))

(defn lazy-test [nums]
  (doall (filter even? (map inc nums))))

(println "Eager vs. Lazy filter+map, N=1000,000 10 repetitions")
(println "Eager test: " (benchmark eager-test 1000000 10) "ms")
(println "Lazy test: " (benchmark lazy-test 1000000 10) "ms")


