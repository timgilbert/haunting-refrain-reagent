(ns haunting-refrain.handlers
  (:require [haunting-refrain.foursquare :as foursquare]
            [haunting-refrain.local-storage :as local-storage]
            ;[environ.core :refer [env]]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler debug
                                   dispatch]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]])
  (:require-macros [reagent.ratom :refer [reaction]]))

; cribbing from https://github.com/Day8/re-frame/blob/master/examples/simple/src/simpleexample/core.cljs

(defn init-handler [db [_ initial-state local-storage-keys]]
  (inspect initial-state)
  (inspect local-storage-keys)
  (merge db initial-state (local-storage/retrieve local-storage-keys)))

(defn goto-handler [db [_ new-page]]
  (assoc db :current-page new-page))

(defn redirect-to-foursquare [_ _]
  (foursquare/redirect-to-foursquare!))

(defn foursquare-token-handler [db [_ token]]
  (assoc db :foursquare-token token)
  (dispatch [:go-to-page :playlist])
  db)

(defn localstorage-get-foursquare 
  "Called when a value for the foursquare toekn is found in localstorage"
  [db [_ token]]
  (assoc-in db [:foursquare :token] token))

;; Set up all event handlers and fire off initial event
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
    :redirect-to-foursquare
    debug
    redirect-to-foursquare)

  (register-handler
    :foursquare-got-token
    debug
    foursquare-token-handler)

  ; Fire off the first event
  (dispatch [:initialize initial-state local-storage-keys])

)
