(ns clojure-examples.brave-macros)

;;; Check http://www.braveclojure.com/writing-macros/

(macroexpand '(when (< 1 2) (println "hello") 4))
(clojure.repl/source when)
