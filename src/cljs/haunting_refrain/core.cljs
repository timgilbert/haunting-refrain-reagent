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


;;; ----------------------------------------------------------------------
;;; Main

(defn main []
  (routing/init! nil)
  (handlers/init! initial-state)
  (reagent/render-component [views/splash] (.-body js/document)))
