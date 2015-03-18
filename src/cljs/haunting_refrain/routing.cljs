(ns haunting-refrain.routing
  (:require [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :refer [dispatch]])
  (:import goog.History))

; http://squirrel.pl/blog/2014/05/01/navigation-and-routing-with-om-and-secretary/

(defn go-home! []
  (let [h (History.)]
    (.replaceToken h "/")))

(defn init! []
  (secretary/set-config! :prefix "#")

  (let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true)))

  (secretary/defroute "/" []
    (console/log "secretary: /")
    (dispatch [:go-to-page :splash]))

  (secretary/defroute "/foursquare-login" []
    (console/log "secretary: /")
    (dispatch [:go-to-page :foursquare-login]))

  (secretary/defroute "/playlist" []
    (console/log "secretary: /playlist")
    (dispatch [:go-to-page :playlist]))

  (secretary/defroute #"/foursquare-callback#access_token=([^&]+)" [token]
    (console/log "got token from foursquare:" token)
    (dispatch [:foursquare-save-token token])))
