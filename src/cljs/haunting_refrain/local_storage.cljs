(ns haunting-refrain.local-storage
  (:require [hodgepodge.core :as hodgepodge]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

(defn get-local [local-storage-path]
  "Given a path in localstorage and an assoc-in path, return a map from the 
  result keyword to the localstorage value"
  (get hodgepodge/local-storage local-storage-path))

(defn key->local [[local-storage-path result-path]] 
  "Given a path in localstorage and an assoc-in path, return a map from the 
  result keyword to the localstorage value"
  (assoc-in {} result-path (get-local local-storage-path)))

(defn retrieve 
  "Look for relevant settings in localstorage, and return them as a map"
  ; {:foursquare-token [:foursquare :token]
  ;  :other-localstorage [:some :other :path]}
  [local-storage-keys]
  (let [pairs (map key->local local-storage-keys)]
    (reduce conj pairs)))
