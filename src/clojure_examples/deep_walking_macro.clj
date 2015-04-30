(ns clojure-examples.deep-walking-macro)

(comment

  (only-ints 1 2 3 4) => [1 2 3 4]

  )

(defmacro only-ints [& args]
  (assert (every? integer? args))
  (vec args)
  )

(defn only-ints-fn  [& args]
  (assert (every? integer? args))
  (vec args))

;; following function will fail at compile time if we call only-ints macro inside it
;; but fails at runtime if we use only-ints-fn
#_(defn test-fn [x]
  (only-ints x))

;; write macro for "if-do"
(defmacro when [test & body]
  (println &form)
  (println &env)
  `(if ~test
     (do ~@body)
     nil))


;;; Deep walking macro
(defmulti parse-item (fn [form ctx]
                       (cond
                        (seq? form) :seq
                        (integer? form) :int
                        (symbol? form) :symbol
                        (nil? form) :nil)))

(defmulti parse-sexpr (fn [[sym & rest] ctx]
                        sym))

(defmethod parse-sexpr 'if
  [[_ test then else] ctx]
  {:type :if
   :test (parse-item test ctx)
   :then (parse-item then ctx)
   :else (parse-item else ctx)})

(defmethod parse-sexpr 'do
  [[_ & body] ctx]
  {:type :do
   :body (map (fn [x] (parse-item x ctx))
              body)})


(defmethod parse-item :seq
  [form ctx]
  (let [form (macroexpand form)]
    (parse-sexpr form ctx)))

(defmethod parse-item :int
  [form ctx]
  {:type :int
   :value form})

(defmethod parse-item :symbol
  [form ctx]
  {:type :symbol
   :value form})

(defmethod parse-item :nil
  [form ctx]
  {:type :nil})
