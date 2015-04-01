(ns haunting-refrain.routing
  (:require [haunting-refrain.local-storage :as local-storage]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [secretary.core :as secretary :include-macros true]
            [domkm.silk :as silk]
            [bidi.bidi :as bidi]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :refer [dispatch]])
  (:import goog.History))

; http://squirrel.pl/blog/2014/05/01/navigation-and-routing-with-om-and-secretary/

(def foursquare-regex #"foursquare-callback#access_token=([^&]+)")

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

(defn url-route-handler [db _]
  "This handler gets called when the page first loads. It is intended to look at the URL 
  the user is on and restore them to the proper state if necessary. This handler will be 
  called after localStorage has been retrieved."
  (let [path (current-page-url!)
        urlmap (into {} (map url-to-page (get-in db [:navigation :states])))
        selected-page (get urlmap path)
        foursquare-match (re-find foursquare-regex path)]
    (console/log "path:" path "selected-page:" (str selected-page))
    ;(inspect urlmap "urlmap")
    (cond
      (= (count foursquare-match) 2) 
        (let [token (second foursquare-match)]
          ; save the token to local storage
          (local-storage/save-foursquare-token! token)
          (redirect-home! (assoc-in db [:foursquare :token] token)))
      selected-page
        (assoc-in db [:navigation :current-page] selected-page)
      (= path "")
        (redirect-home! db)
      :else
        (do 
          (console/warn "Can't figure out what page maps to" path "- redirecting home")
          (redirect-home! db)))))

(comment
(defn go-home! []
  (let [h (History.)]
    (.replaceToken h "/")))

(def skr
  (silk/routes {:splash [[]]
                :playlist [["playlist"]]
                :foursquare [[(silk/regex :token foursquare-regex)]]}))

(defn init! []
  )

(defn secretary-init! []
  (secretary/set-config! :prefix "#")

  (let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true)))

  (secretary/defroute "/" []
    (console/log "secretary: /")
    (dispatch [:go-to-page :splash]))

  (secretary/defroute "/foursquare-login" []
    (console/log "secretary: /")
    (dispatch [:go-to-page :foursquare-login]))

  (secretary/defroute "/playlist" []
    (console/log "secretary: /playlist")
    (dispatch [:go-to-page :playlist]))

  #_(secretary/defroute foursquare-regex [token]
    (console/log "got token from foursquare:" token)
    (dispatch [:foursquare-save-token token])))
)