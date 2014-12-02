(defproject haunting-refrain "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [;; lein new reagent
                 [org.clojure/clojure "1.6.0"]
                 [reagent "0.4.3"]
                 [reagent-utils "0.1.0"]
                 [secretary "1.2.1"]
                 [org.clojure/clojurescript "0.0-2371" :scope "provided"]
                 [com.cemerick/piggieback "0.1.3"]
                 [weasel "0.4.2"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.2"]
                 [prone "0.6.0"]
                 [compojure "1.2.2"]
                 [selmer "0.7.6"]
                 [environ "1.0.0"]
                 [leiningen "2.5.0"]
                 [figwheel "0.1.5-SNAPSHOT"]
                 [prone "0.6.0"]
                 ;; local stuff
                 [cljs-http "0.1.21"]
                 [kioo "0.4.0"]
                 [shodan "0.4.1"]]

  :plugins [
            [lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]
            [lein-ring "0.8.13"]
            [lein-asset-minifier "0.2.0"]]

  :ring {:handler haunting-refrain.handler/app}

  :min-lein-version "2.5.0"

  :uberjar-name "haunting-refrain.jar"

  :minify-assets
  {:assets
    {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :source-map    "resources/public/js/out.js.map"
                                        :externs       ["react/externs/react.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns haunting-refrain.handler
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [pjstadig/humane-test-output "0.6.0"]]

                   :plugins [[lein-figwheel "0.1.4-SNAPSHOT"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:dev? true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}}

             :uberjar {:hooks [leiningen.cljsbuild minify-assets.plugin/hooks]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                             {:source-paths ["env/prod/cljs"]
                                              :compiler
                                              {:optimizations :advanced
                                               :pretty-print false}}}}}

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}}})
