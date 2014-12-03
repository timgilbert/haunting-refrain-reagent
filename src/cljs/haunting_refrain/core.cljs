(ns haunting-refrain.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [haunting-refrain.foursquare :as foursquare]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [reagent.core :as reagent]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; State
(defonce app-state
  (reagent/atom {:text "Hello, this is: "
                 :foursquare-access-token nil}))

(defn get-state [k & [default]]
  (clojure.core/get @app-state k default))

(defn put! [k v]
  (swap! app-state assoc k v))

;; -------------------------
;; Views

(defmulti page identity)

(defmethod page :page1 [_]
  [:div [:h2 (get-state :text) "Page 1"]
   [:div [:a {:href "#/page2"} "go to page 2"]]
   [:div "Token:" (get-state :foursquare-access-token)]
   [:div [:button#login {:on-click foursquare/redirect-to-foursquare!} "Log In"]]
   [:div [:button#logout {:on-click foursquare-logout!} "Log Out"]]])

(defmethod page :page2 [_]
  [:div [:h2 (get-state :text) "Page 2"]
   [:div [:a {:href "#/"} "go to page 1"]]])

(defmethod page :auth-success [_]
  [:div [:h2 (get-state :text) "Success!"]
   [:div [:a {:href "#/"} "go to page 1"]]])

(defmethod page :default [_]
  [:div "Invalid/Unknown route"])

(defn main-page []
  [:div [page (get-state :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (inspect "State:" (clj->js @app-state))
  (put! :current-page :page1))

(secretary/defroute "/page2" []
  (put! :current-page :page2))

(secretary/defroute #"/foursquare-callback#access_token=([^&]+)" [token]
  (foursquare-login! token))

;; -------------------------
;; login/logout
(defn foursquare-login! [token]
  (console/debug "Logged in, token:" token)
  (put! :foursquare-access-token token)
  (.replace js/window.location "#/"))

(defn foursquare-logout! [token]
  (console/debug "Logged out")
  (put! :foursquare-access-token nil))

;; -------------------------
;; Initialize app
(defn init! []
  (reagent/render-component [main-page] (.getElementById js/document "app")))

;; -------------------------
;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
;; need to run this after routes have been defined
(hook-browser-navigation!)
