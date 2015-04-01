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

(defn redirect-home! [db]
  "Set the browser's URL to / and return the DB with the splash page active"
  (let [history (.-history js/window)]
    (.replaceState history "" "" "#/")
    (assoc-in db [:navigation :current-page] :splash)))

(defn- current-page-url! []
  (let [hash (.. js/document -location -hash)]
    (subs hash 1)))

(defn- url-to-page [[page-name {:keys [url]}]]
  [url page-name])

(defn save-foursquare-token [db token]
  ; save the token to local storage
  (local-storage/save-foursquare-token! token)
  ; Redirect the user and replace the URL
  (redirect-home! (assoc-in db [:foursquare :token] token)))

(defn url-route-handler [db _]
  "This handler gets called when the page first loads. It is intended to look at the URL 
  the user is on and restore them to the proper state if necessary. This handler will be 
  called after localStorage has been retrieved."
  (let [path (current-page-url!)
        urlmap (into {} (map url-to-page (get-in db [:navigation :states])))
        selected-page (get urlmap path)
        foursquare-match (re-find routing/foursquare-regex path)]
    (console/log "path:" path "selected-page:" (str selected-page))
    ;(inspect urlmap "urlmap")
    (cond
      (= (count foursquare-match) 2) 
        (save-foursquare-token db (second foursquare-match))
      selected-page
        (assoc-in db [:navigation :current-page] selected-page)
      (= path "")
        (redirect-home! db)
      :else
        (do 
          (console/warn "Can't figure out what page maps to" path "- redirecting home")
          (redirect-home! db)))))

(defn open-login-handler [db [_ url]]
  (let [features "height=400,width=500,menubar=no,location=yes,resizable=yes,scrollbars=no,status=yes"]
    (.open js/window url "loginWindow" features)
    db))

(defn goto-handler [db [_ new-page]]
  (assoc db :current-page new-page))

(defn redirect-to-foursquare [_ _]
  (foursquare/redirect-to-foursquare!))

(defn foursquare-token-handler [db [_ token]]
  "Called when the page starts on a foursquare callback URL."
  (routing/go-home!)
;  (dispatch [:go-to-page :playlist])
  (assoc-in db [:foursquare :token] token))

(defn foursquare-logout-handler [db [_]]
  "Called when the page starts on a foursquare callback URL."
  (assoc-in db [:foursquare :token] nil))

(defn localstorage-get-foursquare 
  "Called when a value for the foursquare toekn is found in localstorage"
  [db [_ token]]
  (assoc-in db [:foursquare :token] token))

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
    url-route-handler)

  (register-handler
    :open-login-window
    debug
    open-login-handler)

  (register-handler
    :redirect-to-foursquare
    debug
    redirect-to-foursquare)

  (register-handler
    :foursquare-got-token
    debug
    foursquare-token-handler)

  (register-handler
    :foursquare-logout
    debug
    foursquare-logout-handler)

  ; Fire off the first event
  (dispatch [:initialize initial-state local-storage-keys])
  ; Set page state from URL
  (dispatch [:route])

)
