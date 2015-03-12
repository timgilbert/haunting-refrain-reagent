(ns haunting-refrain.views
  (:require [haunting-refrain.foursquare :as foursquare]
            [cljs.core.async :refer [put! chan <!]]
            [kioo.core :refer [handle-wrapper]]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; -------------------------
;; kioo templates

(defn onclick [& args]
  (console/log "click!")
  (console/log args))

(defsnippet login-button "templates/splash.html" [:.login] []
  {[:button] (listen :on-click foursquare/redirect-to-foursquare!)})

(deftemplate splash "templates/splash.html" []
  {[:.login] (substitute [login-button])})

(deftemplate playlist "templates/playlist.html" []
  {[:.login] (substitute [login-button])})

;; -------------------------
;; Views

(defmulti page identity)

(defn main-page []
  [:div [page (get-state :current-page)]])

(defmethod page :splash [_]
  (splash))


