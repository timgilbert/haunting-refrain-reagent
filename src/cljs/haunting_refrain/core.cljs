(ns haunting-refrain.core
  (:require [haunting-refrain.foursquare :as foursquare]
            [haunting-refrain.views :as views]
            [haunting-refrain.handlers :as handlers]
            [haunting-refrain.routing :as routing]
            [reagent.core :as reagent]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

;;; ----------------------------------------------------------------------
;;; Initial app state

(defonce initial-state 
  {:navigation {:current-page :splash
                :states {:splash 
                          {:url "/" :name "Splash"}
                         :foursquare-callback 
                          {:url "foursquare-callback" :name "callback"}
                         :playlist 
                          {:url "/playlist" :name "Playlist"}}}
   :foursquare {:token nil}})

;; Set of localstorage keyword to (get-in) index into app-state map
(defonce local-storage-keys
  {:foursquare-token [:foursquare :token]})

;;; ----------------------------------------------------------------------
;;; Main

(defn main []
  (routing/init!)
  (handlers/init! initial-state local-storage-keys)
  (reagent/render-component [views/shell] (.-body js/document)))
