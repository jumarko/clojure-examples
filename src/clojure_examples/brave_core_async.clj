(ns clojure-examples.brave-core-async
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]))
;;;;
;;;; core.async tutorial from the "Clojure for the Brave and True"
;;;; Check http://www.braveclojure.com/core-async/
;;;;

;;; Let's start
(def echo-chan (chan))
(go (println (<! echo-chan)))
(>!! echo-chan "ketchup")


;;; Buffering
;;; Avoiding blocking - "Ketchup chef"
(def echo-buffer (chan 2))
(>!! echo-buffer "ketchup")
(>!! echo-buffer "ketchup")
;; following blocks because the channel buffer is full
#_(>!! echo-buffer "ketchup")

;; sliding buffers - FIFO
;; Note: make sure to wrap the result of a/sliding-buffer with chan
(def sliding-channel (chan (a/sliding-buffer 2)))
(>!! sliding-channel "ketchup")
(>!! sliding-channel "milk")
(>!! sliding-channel "bread")
(<!! sliding-channel)
(<!! sliding-channel)

;; dropping buffers - LIFO
(def dropping-channel (chan (a/dropping-buffer 2)))
(>!! dropping-channel "ketchup")
(>!! dropping-channel "milk")
(>!! dropping-channel "bread")
(<!! dropping-channel)
(<!! dropping-channel)


;;; Blocking and parking
;;; You can create thousand processes without much overhead!
;;; Blocking - thread is waiting but remains alive -> you need to use another thread for execution
;;; Parking - frees up the thread so it can kep doing work
(def hi-chan (chan))
;; 1000 processes are waiting for another process to take from hi-chan
(doseq [n (range 1000)]
  (go (>! hi-chan (str "hi " n))))
;; finally another 1000 processes take message from hi-chan
(doseq [n (range 1000)]
  (go (println  (<! hi-chan))))


;;; thread
;;; Use with blocking operations when your process will take a long time before putting or taking
(thread (println (<!! echo-chan)))
(>!! echo-chan "mustard")

;; when thread process stops the process'es return value is put on the channel that thread returns
(let [t-chan (thread "chili")]
  (<!! t-chan))


;;; Hot dog machine
(defn hot-dog-machine []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hot dog"))
    [in out]))

(let [[in out] (hot-dog-machine)]
  (>!! in "pocket lint")
  (<!! out))

;; let's improve the hotdog machine to accept money only
;; and be able to dispense more than one hot dog
(defn hot-dog-machine-v2 [hot-dog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hot-dog-count]
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hot dog")
                    (recur (dec hc)))
                (do (>! out "wilted lettuce")
                    (recur hc))))
            (do (close! in)
                (close! out)))))
    [in out]))


(let [[in out] (hot-dog-machine-v2 2)]
  (>!! in "pocket lint")
  (println (<!! out))

  (>!! in 3)
  (println (<!! out))

  (>!! in 3)
  (println (<!! out))

  (>!! in 3)
  (println (<!! out)))


;;; pipeline
(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (go (>! c2 (clojure.string/upper-case (<! c1))))
  (go (>! c3 (clojure.string/reverse (<! c2))))
  (go (println (<! c3)))
  (>!! c1 "redrum"))


;;; alts!!
;;; lets you use the result of first successful channel operation among a collection of operations
(defn upload [headshot c]
  (go (Thread/sleep (rand 100))
      (>! c headshot)))

(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (upload "serious.jpg" c1)
  (upload "fun.jpg" c2)
  (upload "sassy.jpg" c3)
  (let [[headshot channel] (alts!! [c1 c2 c3])]
    (println "Sending headshot notification for" headshot)))

;; you can also use timeout channel
(let [c1 (chan)]
  (upload "serious.jpg" c1)
  (let [[headshot channel] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for" headshot)
      (println "Timed out!"))))

;; you can also use alts!! to specify put operations
(let [c1 (chan)
      c2 (chan)]
  (go (<! c2))
  (let [[value channel] (alts!! [c1 [c2 "put!"]])]
    (println value)
    (= channel c2)))


;;; Queues
;;; Let's say you want to get a bunch of random quotes from a website and write them to a single file
;;; You want to make sure that only one quote is written to a file at a time so the text doesn't get interleaved,
;;; so you put hyour quotes on a queue.
(defn append-to-file
  "Write a string to the end of a file"
  [filename s]
  (spit filename s :append true))

(defn format-quote
  "Delineate the beginning and end of a quote because it's convenient"
  [quote]
  (str "===BEGING QUOTE===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  "Retrieve a random quote and format it"
  []
  (format-quote (slurp "http://www.braveclojure.com/random-quote")))

(defn snag-quotes [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))

(snag-quotes "quotes.txt" 10)


;;; Escape Callback Hell with Process Pipelines
(defn upper-caser [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer [in]
  (go (while true (println (<! in)))))

(def in-chan (chan))
(def upper-caser-out (upper-caser in-chan))
(def reverser-out (reverser upper-caser-out))
(printer reverser-out)
(>!! in-chan "redrum")
(>!! in-chan "repaid")
