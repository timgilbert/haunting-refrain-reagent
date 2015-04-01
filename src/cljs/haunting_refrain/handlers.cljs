(ns haunting-refrain.handlers
  (:require [haunting-refrain.foursquare :as foursquare]
            [haunting-refrain.local-storage :as local-storage]
            [haunting-refrain.routing :as routing]
            ;[environ.core :refer [env]]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler debug
                                   dispatch]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [cljs-time.core :refer [today minus weeks]]
            [cljs-time.coerce :refer [to-date]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn- default-date-ranges! []
  (let [end-date (today)
        start-date (minus end-date (weeks 1))]
    {:date-range {:start (to-date start-date) :end (to-date end-date)}}))

; cribbing from https://github.com/Day8/re-frame/blob/master/examples/simple/src/simpleexample/core.cljs

(defn init-handler [db [_ initial-state local-storage-keys]]
  (merge db 
         initial-state 
         (local-storage/retrieve local-storage-keys)
         (default-date-ranges!)))

(defn goto-handler [db [_ new-page]]
  (assoc db :current-page new-page))

(defn new-date-handler [db [_ which date]]
  "#(dispatch [:new-date :start %])"
  (assoc-in db [:date-range which] date))

(defn redirect-to-foursquare [_ _]
  (foursquare/redirect-to-foursquare!))

(defn foursquare-logout-handler [db [_]]
  "Called when the page starts on a foursquare callback URL."
  (local-storage/save-foursquare-token! nil)
  (assoc-in db [:foursquare :token] nil))

;; Set up all event handlers and fire off initial events
(defn init! [initial-state local-storage-keys]
  (register-handler
    :initialize
    debug
    init-handler)

  (register-handler
    :go-to-page
    debug
    goto-handler)

  (register-handler
    :route
    debug
    routing/url-route-handler)

  (register-handler
    :redirect-to-foursquare
    debug
    redirect-to-foursquare)

  (register-handler
    :foursquare-logout
    debug
    foursquare-logout-handler)

  (register-handler
    :new-date
    debug
    new-date-handler)

  ; Fire off the first event
  (dispatch [:initialize initial-state local-storage-keys])
  ; Set page state from URL
  (dispatch [:route]))
