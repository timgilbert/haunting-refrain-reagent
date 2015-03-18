(ns haunting-refrain.local-storage
  (:require [hodgepodge.core :as hodgepodge]
            [re-frame.core :refer [dispatch]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

(defn path->local [[local-storage-path result-path]] 
  "Given a path in localstorage and an assoc-in path, return a map from the 
  result keyword to the localstorage value"
  (assoc-in {} result-path (get hodgepodge/local-storage local-storage-path)))

(defn retrieve 
  "Look for relevant settings in localstorage, and return them as a map"
  ; {:foursquare-token [:foursquare :token]
  ;  :other-localstorage [:some :other :path]}
  [local-storage-keys]
  (let [pairs (map path->local local-storage-keys)]
    ;(inspect pairs "pairs")
    ;(inspect (apply merge pairs) "merge")
    (apply merge pairs)))

(defn event-listener [event]
  "Called when a storage event is fired. Note that this event currently only 
  fires when the storage change is triggered by another window, and when the value 
  actually changes."
  (inspect event "storage event")
  (console/log "key:" (.-key event) (.-newValue event))
  (dispatch [:local-storage (.-key event) (.-newValue event)]))

(defn init! []
  (.addEventListener js/window "storage" event-listener))