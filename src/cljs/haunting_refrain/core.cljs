(ns haunting-refrain.core
  (:require [haunting-refrain.foursquare :as foursquare]
            [haunting-refrain.views :as views]
            [haunting-refrain.handlers :as handlers]
            [haunting-refrain.local-storage :as local-storage]
            [haunting-refrain.routing :as routing]
            [reagent.core :as reagent]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

;;; ----------------------------------------------------------------------
;;; Initial app state

(defonce initial-state 
  {:navigation {:current-page nil
                :states {:splash 
                          {:url "/" :name "Splash"}
                         :about
                           {:url "/about" :name "About"}
                         :playlist 
                          {:url "/playlist" :name "Playlist"}}}
   :foursquare {:token nil}
   :date-range {:start nil :end nil}})

;; Set of localstorage keywords to (get-in) index into app-state map
(defonce local-storage-keys
  {:foursquare-token [:foursquare :token]})

;;; ----------------------------------------------------------------------
;;; Main

(defn main []
  "Main program. Note that this is typically only called once per page reload"
  (handlers/init! initial-state local-storage-keys)
  ;(routing/init!)
  (views/init!)
  (reagent/render-component [views/shell] (.-body js/document)))
