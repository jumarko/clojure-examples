(ns clojure-spec-example.core
  (:gen-class)
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [clojure.spec.test :as stest])
  (:import java.util.Date))



;;;; Examples from http://clojure.org/about/spec
(s/def ::even? (s/and integer? even?))
(s/def ::odd? (s/and integer? odd?))
(s/def ::a integer?)
(s/def ::b integer?)
(s/def ::c integer?)
(def s (s/cat :forty-two #{42}
              :odds (s/+ ::odd?)
              :m (s/keys :req-un [::a ::b ::c])
              :oes (s/* (s/cat :o ::odd? :e ::even?))
              :ex (s/alt :odd ::odd? :even ::even?)))

(def my-seq [42
             1 3
             {:a 1 :b 2 :c 3}
             1 2 3 42 43 44
             11])
(s/conform s my-seq)
(s/explain s my-seq)





;;;; Examples from http://clojure.org/guides/spec
(s/valid? even? 10)
(s/valid? string? "abc")
(s/valid? #(> % 5) 10)
(s/valid? #{:club :diamond :heart :spade} :club)

;; Registry can be used for globally defined reusable specs
(s/def ::date inst?)
(s/def ::suit #{:club :diamond :heart :spade})
(s/valid? ::date (Date.))
(s/valid? ::suit :club)
(s/valid? ::suit :srdce)

;; precicates composition
(s/def ::big-even (s/and int? even? #(> % 1000)))
(s/valid? ::big-even :foo)
(s/valid? ::big-even 10)
(s/valid? ::big-even 10000)

; notice the annotation of each choice with a tag
(s/def ::name-or-id (s/or :name string?
                          :id int?))
(s/valid? ::name-or-id "abc")
(s/valid? ::name-or-id 100)
(s/valid? ::name-or-id :foo)
(s/conform ::name-or-id "abc")

;; nilable can be used to allow nil as a valid value together with string?, number? et al.
(s/valid? string? nil)
(s/valid? (s/nilable string?) nil)

;; explain can be used to report to stdout why a value does not conform to spec
(s/explain ::suit 42)
(s/explain ::big-even 5)
(s/explain ::name-or-id :foo)

;; you can also use eplain-str and explain-data
(s/explain-str ::name-or-id :foo)
(s/explain-data ::name-or-id :foo)

;; sequences and standard regular expression operators
(s/def ::ingredient (s/cat :quantity number? :unit keyword?))
(s/conform ::ingredient [2 :teaspoon])
(s/explain ::ingredient [11 "peaches"])
(s/explain ::ingredient [11])

(s/def ::odds-then-maybe-even (s/cat :odds (s/+ odd?)
                                     :even (s/? even?)))
(s/conform ::odds-then-maybe-even [1 3 5 100])
(s/conform ::odds-then-maybe-even [1])
(s/explain ::odds-then-maybe-even [100])

(s/def ::config (s/*
                 (s/cat :prop string?
                        :val (s/alt :s string? :b boolean?))))

(s/conform ::config ["-server" "foo" "-verbose" true "-user" "joe" "password" "dumb"])

(s/describe ::config)


;; additional regex operator "&" which takes a regex operator and constrains it with one or more additional predicates
(s/def ::even-strings (s/& (s/* string?) #(even? (count %))))
(s/valid? ::even-strings ["a"])
(s/valid? ::even-strings ["a" "b"])
(s/valid? ::even-strings ["a" "b" "c"])
(s/explain ::even-strings ["a" "b" "c"])


;; When regex ops are combined, they describe a single sequence. If you need to spec a nested sequential collection, you must use an explicit call to spec to start a new nested regex context.
;; Let's describe a sequence like [:names ["a" "b"] :nums [1 2 3]]
(s/def ::nested
  (s/cat :names-kw #{:names}
         :names (s/spec (s/* string?))
         :nums-kw #{:nums}
         :nums (s/spec (s/* number?))))
(s/conform ::nested [:names ["a" "b"] :nums [1 2 3]])
(s/explain ::nested [:names ["a" "b"] :nums [1 2 "c"]])


;; Entity Maps
;; clojure.spec assign meaning to individual attributes, then collects them into maps

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))
(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

(s/valid? ::person
          {::first-name "Elon"
           ::last-name "Musk"
           ::email "elon@example.com"})

;; fails required key check
(s/explain ::person {::first-name "Elon"})

;; fails attribute conformance
(s/explain ::person
           {::first-name "Elon"
            ::last-name "Musk"
            ::email "n/a"})

;; we can also check for unqualified keys
(s/def :unq/person
  (s/keys :req-un [::first-name ::last-name ::email]
          :opt-un [::phone]))

(s/conform :unq/person
           {:first-name "Elon"
            :last-name "Musk"
            :email "elon@example.com"})

(s/explain :unq/person
           {:first-name "Elon"
            :last-name "Musk"
            :email "n/a"})

(s/explain :unq/person
           {:first-name "Elon"})

;; unqualified keys can also be used to validate record attributes
(defrecord Person [first-name last-name email phone])

(s/explain :unq/person
           (->Person "Elon" nil nil nil))

(s/conform :unq/person
           (->Person "Elon" "Musk" "elon@example.com" nil))



;; Collections
(s/conform (s/coll-of keyword? []) [:a :b :c])
(s/conform (s/coll-of number? #{}) #{5 10 2})

;; Tuples
(s/def ::point (s/tuple double? double? double?))
(s/conform ::point [1.5 2.5 -0.5])


;; Using spec for validation

;; one way is to use s/valid?
(defn person-name [person]
  {:pre [(s/valid? ::person person)]
   :post [(s/valid? string? %)]}
  (str (::first-name person) " " (::last-name person)))

(person-name 42)

(person-name {::first-name "Elon" ::last-name "Musk" ::email "elon@example.com"})

;; deeper level of integration is to use conform
(defn- set-config [prop val]
  (println "set" prop val))
(defn configure [input]
  (let [parsed (s/conform ::config input)]
    (println parsed)
    (if (= parsed ::s/invalid)
      (throw (ex-info "Invalid input" (s/explain-data ::config input)))
      (for [{prop :prop [_ val] :val} parsed]
        (set-config (subs prop 1) val)))))
(configure ["-server" "foo" "-verbose" true "-user" "joe"])
(configure ["-server" "foo" "-verbose" true "-user" "joe" "-trailing_option"])


;;; we can define input and output specifications for a function or macro via fdef

(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

;; specification for ranged-rand
(s/fdef ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

;; we can turn on instrumentation (spec checking):
(s/instrument #'ranged-rand)

(ranged-rand 8 5)

;; the instrumentation can also be truned on more widely using "instrument-ns" and "instrumenet-all


;; Higher-order functions

(defn adder [x] #(+ x %))

(s/fdef adder
        :args (s/cat :x number?)
        :ret (s/fspec :args (s/cat :y number?)
                      :ret number?)
        :fn #(= (-> % :args :x) ((:ret %) 0)))


;; Macros
;; macroexpander will look for and conform :args specs registered for macros at expansion time. If an error is detected, explain will be invoked to explain the error.
(s/fdef clojure.core/declare
        :args (s/cat :names (s/* simple-symbol?))
        :ret ::s/any)

(declare 100)



;; Gmae of cards - a bigger set of specs

(def suit? #{:club :diamond :heart :spade})
(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def deck (for [suit suit? rank rank?] [rank suit]))

(s/def ::card (s/tuple (s/tuple rank? suit?)))
(s/def ::hand (s/* ::card))

(s/def ::name string?)
(s/def ::score int?)
(s/def ::player (s/keys :req [::name ::score ::hand]))

(s/def ::players (s/* ::player))
(s/def ::deck (s/* ::card))
(s/def ::game (s/keys :req [::players ::deck]))

;; Now, validate a piece of this data gainst the schema
(def kenny
  {::name "Kenny Rogers"
   ::score 100
   ::hand []})
(s/valid? ::player kenny)

(s/explain ::game
           {::deck deck
            ::players [{::name "Kenny Rogers"
                        ::score 100
                        ::hand [[2 :banana]]}]})


;;; Generators
;;;

;; generate a single sample value
(gen/generate (s/gen int?))
(gen/generate (s/gen nil?))
(gen/generate (s/gen ::player))
(gen/generate (s/gen ::game))

;; generate series of samples
(gen/sample (s/gen string?))
(gen/sample (s/gen #{:club :diamond :heart :spade}))
(gen/sample (s/gen (s/cat :k keyword? :ns (s/+ number?))))


;; "excercise" returns pairs of generated and conformed values for a spec
(s/exercise (s/cat :k keyword? :ns (s/+ number?)) 3)


;; sometimes the generators need additional help
(gen/generate (s/gen even?))

;; => spec is designed to support this case via and
(gen/generate (s/gen (s/and int? even?)))

(defn divisible-by [n] #(zero? (mod % n)))
(gen/sample (s/gen (s/and int?
                          pos?
                          (divisible-by 3))))

;; going too far with refinement will throw an error, because predicate cannot be resolved with a relatively small number of attempts.
(gen/sample (s/gen (s/and string? #(clojure.string/includes? % "hello"))))


;;; Building custom generator
;;; 3 ways in decreasing order of preference
;;; 1. Let spec create a generator based on a predicate/spec
;;; 2. Create your own generator from the tools in clojure.spec.gen
;;; 3. Use test.check or other test.check compatible libraries (like test.chuck)

;; 1.option
(s/def ::kws (s/and keyword? #(= (namespace %) "my.domain")))
(s/valid? ::kws :my.domain/name)
(gen/sample (s/gen ::kws)) ;=> unlikely to generate useful keywords

(def kw-gen (s/gen #{:my.domain/name :my.domain/occupation :my.domain/id}))
(gen/sample kw-gen 5)

;; use with-gen to redefine our spec using custom generator
(s/def ::kws (s/with-gen (s/and keyword? #(= (namespace %) "my.domain"))
               #(s/gen #{:my.domain/name :my.domain/occupation :my.domain/id})))
(s/valid? ::kws :my.domain/name)
(gen/sample (s/gen ::kws))

;; simple way is to use fmap to build up a keyword based on generated strings
(def kw-gen-2 (gen/fmap #(keyword "my.domain" %) (gen/string-alphanumeric)))
(gen/sample kw-gen-2 5)

;; small fix to avoid empty strings (empty string is not a valid keyword)
(def kw-gen-3 (gen/fmap #(keyword "my.domain" %)
                        (gen/such-that #(not= % "")
                                       (gen/string-alphanumeric))))
(gen/sample kw-gen-3 5)


;; back to our "hello" example
(s/def ::hello
  (s/with-gen #(clojure.string/includes? % "hello")
    (fn [] (gen/fmap (fn [s] (let [i (rand-int (count s))]
                             (str (subs s 0 i) "hello" (subs s i))))
                    (gen/string-alphanumeric)))))
(gen/sample (s/gen ::hello))



;;; Range specs and generators

;; range of integer values
(s/def ::roll (s/int-in 0 11))
(gen/sample (s/gen ::roll))

;; range of instants
(s/def ::the-aughts (s/inst-in #inst "2000" #inst "2010"))
(drop 50 (gen/sample (s/gen ::the-aughts) 55))

;; range of doubles
(s/def ::dubs (s/double-in :min -100.0 :max 100.0 :NaN? false :infinity? false))
(s/valid? ::dubs 2.9)
(s/valid? ::dubs Double/POSITIVE_INFINITY)
(gen/sample (s/gen ::dubs))



;;; Testing
;;; When functions have specs with generators, we can use check-var to automatically check the specs are correct
;;; check-var will generate arguments based on the :args spec for a function, invoke the function and validate the :ret and :fn specs were satisfied

(stest/check-var #'ranged-rand)
