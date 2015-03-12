(ns haunting-refrain.core
  (:require [haunting-refrain.foursquare :as foursquare]
            [haunting-refrain.views :as views]
            [haunting-refrain.routing :as routing]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [reagent.core :as reagent]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]))

;;; ----------------------------------------------------------------------
;;; App state

(defonce app-state 
  (reagent/atom 
    {:navigation {:current-page :splash
                  :states {:splash 
                            {:url "/" :name "Splash"}
                           :foursquare-callback 
                            {:url "foursquare-callback" :render views/debug :name "callback"}
                           :playlist 
                            {:url "/playlist" :name "Playlist"}}}
     :foursquare {:token nil}}))

;;; ----------------------------------------------------------------------
;;; Main

(defn main []
  (routing/init! app-state)
  (reagent/render-component [views/main-page] (.-body js/document)))

