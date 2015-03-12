(ns haunting-refrain.handlers
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler debug
                                   dispatch]]))

;; Set up all event handlers
; cribbing from https://github.com/Day8/re-frame/blob/master/examples/simple/src/simpleexample/core.cljs

(defn init-handler [db [_ initial-state]]
  (merge db initial-state))

(defn goto-handler [db [_ new-page]]
  (assoc db :current-page new-page))

(defn foursquare-token-handler [db [_ token]]
  (assoc db :foursquare-token token)
  (dispatch [:goto-page]))

(defn register-all-handlers! []
  (register-handler
    :initialize
    (debug init-handler))

  (register-handler
    :go-to-page
    (debug goto-handler))

  (register-handler
    :foursquare-got-token
    (debug foursquare-token-handler))

  )

(defn init! [initial-state]
  (register-all-handlers!)
  (dispatch [:initialize initial-state]))
