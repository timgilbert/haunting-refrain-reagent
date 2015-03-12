(ns haunting-refrain.views
  (:require [haunting-refrain.foursquare :as foursquare]
            [cljs.core.async :refer [put! chan <!]]
            [re-frame.core :refer [subscribe dispatch]]
            [kioo.core :refer [handle-wrapper]]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]])
  (:require-macros [kioo.reagent :refer [defsnippet deftemplate]]))

;; -------------------------
;; kioo templates

(defn onclick [& args]
  (console/log "click!")
  (console/log args))

(defsnippet login-button "templates/splash.html" [:.login] []
  {[:button] (listen :on-click #(dispatch :redirect-to-foursquare))})

(deftemplate splash "templates/splash.html" []
  {})

(deftemplate splash2 "templates/splash.html" []
  {[:.login] (substitute [login-button])})

(deftemplate playlist "templates/playlist.html" []
  {[:.login] (substitute [login-button])})
