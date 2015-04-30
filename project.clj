(defproject clojure-examples "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.combinatorics "0.1.1"]
                 [bouncer "0.3.2-SNAPSHOT"]
                 [cheshire "5.4.0"]
                 [clj-http "1.1.1"]
                 [clj-time "0.9.0"]
                 [me.raynes/conch "0.8.0"]
                 [midje "1.6.3"]]
  :main ^:skip-aot clojure-examples.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
