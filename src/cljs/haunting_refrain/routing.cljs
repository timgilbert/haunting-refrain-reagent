(ns haunting-refrain.routing
  (:require [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

; http://squirrel.pl/blog/2014/05/01/navigation-and-routing-with-om-and-secretary/

(defn init! [{:keys [navigation] :as app-state}]
  (secretary/set-config! :prefix "#")

  (let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true)))

  (secretary/defroute "/" []
    (console/log "hmm, /"))

  (secretary/defroute "/playlist" []
    (console/log "hmm, /playlist"))

  (secretary/defroute #"/foursquare-callback#access_token=([^&]+)" [token]
    (console/log "got token from foursquare:" token)))
