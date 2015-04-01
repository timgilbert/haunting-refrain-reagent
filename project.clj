(defproject haunting-refrain "0.1.0-SNAPSHOT"
  :description "Playlist generator in re-frame"
  :url "https://github.com/timgilbert/haunting-refrain-om"
  :license {:name "MIT"
            :url "https://github.com/timgilbert/haunting-refrain/MIT-LICENSE.txt"}

  :source-paths ["src/clj"]
  :repl-options {:timeout 200000} ;; Defaults to 30000 (30 seconds)

  :test-paths ["spec/clj"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3169" :scope "provided"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [prone "0.8.1"]
                 [compojure "1.3.2"]
                 [selmer "0.8.2"]
                 [environ "1.0.0"]
                 [prone "0.8.1"]
                 [http-kit "2.1.19"]
                 ;; local stuff
                 [reagent "0.5.0"]
                 [reagent-utils "0.1.4"]
                 [cljs-http "0.1.30"]
                 [kioo "0.4.1-SNAPSHOT" :exclusions [org.clojure/clojure]] ; need this for compat with reagent 0.5.0
                 [re-frame "0.2.0"]
                 [json-html "0.2.8"]
                 [shodan "0.4.1"]
                 [hodgepodge "0.1.3"]
                 [cljsjs/pikaday "1.3.2-0"]
                 [com.andrewmcveigh/cljs-time "0.3.3"]
                 [secretary "1.2.2"]
                 [bidi "1.18.9"]
                 [com.domkm/silk "0.0.4"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]
            [lein-less "1.7.2"]
            [lein-asset-minifier "0.2.0"]]

  :min-lein-version "2.5.0"

  :uberjar-name "haunting-refrain.jar"

  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :source-map    "resources/public/js/out.js.map"
                                        :preamble      ["react/react.min.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns haunting-refrain.server
                   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :source-paths ["env/dev/clj"]
                   :test-paths ["test/clj"]

                   :dependencies [[figwheel "0.2.5"]
                                  [figwheel-sidecar "0.2.5"]
                                  [com.cemerick/piggieback "0.1.5"]
                                  [weasel "0.6.0"]
                                  [pjstadig/humane-test-output "0.7.0"]]

                   :plugins [[lein-figwheel "0.2.1-SNAPSHOT"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:is-dev true}

                   :cljsbuild {:test-commands { "test" ["phantomjs" "env/test/js/unit-test.js" "env/test/unit-test.html"] }
                               :builds {:app {:source-paths ["env/dev/cljs"]}
                                        :test {:source-paths ["src/cljs" "test/cljs"]
                                               :compiler {:output-to     "resources/public/js/app_test.js"
                                                          :output-dir    "resources/public/js/test"
                                                          :source-map    "resources/public/js/test.js.map"
                                                          :preamble      ["react/react.min.js"]
                                                          :optimizations :whitespace
                                                          :pretty-print  false}}}}}


             :uberjar {:source-paths ["env/prod/clj"]
                       :hooks [leiningen.cljsbuild leiningen.less]
                       :env {:production true}
                       :omit-source true
                       :aot :all
                       :cljsbuild {:builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
