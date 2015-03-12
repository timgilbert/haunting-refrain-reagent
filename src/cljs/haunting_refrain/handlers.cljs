(ns haunting-refrain.handlers
  (:require [haunting-refrain.foursquare :as foursquare]
            ;[environ.core :refer [env]]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler debug
                                   dispatch]])
  (:require-macros [reagent.ratom :refer [reaction]]))

; cribbing from https://github.com/Day8/re-frame/blob/master/examples/simple/src/simpleexample/core.cljs

(defn init-handler [db [_ initial-state]]
  (merge db initial-state))

(defn goto-handler [db [_ new-page]]
  (assoc db :current-page new-page))

(defn redirect-to-foursquare [_ _]
  (foursquare/redirect-to-foursquare!))

(defn foursquare-token-handler [db [_ token]]
  (assoc db :foursquare-token token)
  (dispatch [:goto-page :playlist])
  db)

;; Set up all event handlers and fire off initial event
(defn init! [initial-state]
  (register-handler
    :initialize
    (debug init-handler))

  (register-handler
    :go-to-page
    (debug goto-handler))

  (register-handler
    :redirect-to-foursquare
    (debug redirect-to-foursquare))

  (register-handler
    :foursquare-got-token
    (debug foursquare-token-handler))

  (dispatch [:initialize initial-state]))
