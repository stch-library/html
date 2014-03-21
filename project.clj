(defproject stch-library/html "0.1.1"
  :description
  "DSL for HTML generation. Supports Clojure and ClojureScript."
  :url "https://github.com/stch-library/html"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [stch-library/schema "0.3.2"]]
  :profiles {:dev {:dependencies [[speclj "3.0.2"]
                                  [stch-library/dom "0.1.0"]]}}
  :plugins [[speclj "3.0.2"]
            [lein-cljsbuild "1.0.2"]
            [codox "0.6.7"]]
  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["spec"]
  :cljsbuild
  {:builds [{:id "browser-test"
             :source-paths ["src/cljs" "test"]
             :compiler
             {:output-to "resources/js/browser-test.js"
              :optimizations :whitespace
              :pretty-print true}}]})
