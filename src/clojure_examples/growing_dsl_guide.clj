(ns clojure-examples.growing-dsl-guide)

;;; Clojure language guide "Growint a DSL with Clojure".
;;; Check http://clojure-doc.org/articles/tutorials/growing_a_dsl_with_clojure.html
;;;
;;; Goal define a DSL which allows us to generate various scripting languages, e.g. Bash.

;;; Let's start with Bash...
(defn emit-bash-form
  "Takes Clojure form as an input and outputs Bash form."
  [a]
  (cond
    (= (class a) String) a
    ;; notice that under the hood Clojure uses Long data type
    (= (class a) Long) (str a)
    (= (class a) Double) (str a)
    :else (throw (Exception. "Fell through"))))

(emit-bash-form 1)
(emit-bash-form 10.2)
(emit-bash-form "a")

;; But how about "(println ...)" ??
;; In java it's impossible, but with Clojure "code is data"

;; we can use quote to stop evaluation
'(println "a")
(class '(println "a")) ;=> clojure.lang.PersistentList

;; we can now interrogate the raw code as if it were any old Clojure list
(first '(println "a"))
(second '(println "a"))


;;; A Little Closer to Clojure

;; Let's extend our emit-bash-form function
(defn emit-bash-form
  "Takes Clojure form as an input and outputs Bash form."
  [a]
  (cond
    (= (class a) clojure.lang.PersistentList)
    (case (name (first a))
      "println" (str "echo " (second a)))

    (= (class a) String) a
    ;; notice that under the hood Clojure uses Long data type
    (= (class a) Long) (str a)
    (= (class a) Double) (str a)
    :else (throw (Exception. "Fell through"))))

(emit-bash-form '(println "a"))
(emit-bash-form '(println "hello"))


;;; Multimethods to Abstract the Dispatch
;;; Time for some refactoring!
;;; We need to split emit-bash-form function into more manageable pieces

;; multimethods

(defmulti emit-bash
  (fn [form]
    (class form)))

(defmethod emit-bash
  clojure.lang.PersistentList
  [form]
  (case (name (first form))
    "println" (str "echo " (second form))))

(defmethod emit-bash
  java.lang.String
  [form]
  form)

(defmethod emit-bash
  java.lang.Long
  [form]
  (str form))

(defmethod emit-bash
  java.lang.Double
  [form]
  (str form))

(emit-bash '(println "a"))


;;; Extending our DSL for Batch Script
;;; - We are now happy with Bash implementation, Let's extend it for Windows Batch Script.
(defmulti emit-batch
  (fn [form] (class form)))

(defmethod emit-batch clojure.lang.PersistentList
  [form]
  (case (name (first form))
    "println" (str "ECHO " (second form))))

(defmethod emit-batch java.lang.String
  [form]
  form)

(defmethod emit-batch java.lang.Long
  [form]
  (str form))

(defmethod emit-batch java.lang.Double
  [form]
  (str  form))

(emit-batch '(println "a"))


;;; Ad-hoc Hierarchies
;;; There's a lot of similarities in "bash" and "batch" implementation

;; Let's define our hierarchy
(derive ::bash ::common)
(derive ::batch ::common)

(parents ::bash)
(parents ::batch)

;; Utilizing a Hierarchy in Multimethod
(def ^{:dynamic true}
  ;; The current script language implementation to generate
  *current-implementation*)

(defmulti emit
  (fn [form]
    [*current-implementation* (class form)]))

;; Common implementations
(defmethod emit [::common java.lang.String]
  [form]
  form)

(defmethod emit [::common java.lang.Long]
  [form]
  (str form))

(defmethod emit [::common java.lang.Double]
  [form]
  (str form))

;; special implementations
(defmethod emit [::bash clojure.lang.PersistentList]
  [form]
  (case (name (first form))
    "println" (str "echo " (second form))))

(defmethod emit [::batch clojure.lang.PersistentList]
  [form]
  (case (name (first form))
    "println" (str "ECHO " (second form))))

(binding [*current-implementation* ::common] (emit "a"))

(binding [*current-implementation* ::bash] (emit "a"))
(binding [*current-implementation* ::bash] (emit '(println "a")))

(binding [*current-implementation* ::batch] (emit '(println "a")))

;; not supported with common...
(binding [*current-implementation* ::common] (emit '(println "a")))


;;;Icing on the Cake
;;; Let's improve usage of our DSL...

(defmacro script [form]
  `(emit '~form))

;; to evaluate our script form inside a binding form we need to drop it in before evaluation
(defmacro with-implementation [impl & body]
  `(binding [*current-implementation* ~impl]
     ~@body))

(with-implementation ::bash
  (script (println "a")))

(with-implementation ::batch
  (script (println "a")))
