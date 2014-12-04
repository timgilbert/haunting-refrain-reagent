(ns haunting-refrain.core
  (:require [haunting-refrain.foursquare :as foursquare]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [reagent.core :as reagent]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [json-html.core :as json-html]
            [goog.history.EventType :as EventType])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [kioo.reagent :refer [defsnippet deftemplate]])
  (:import goog.History))

;; -------------------------
;; State
(defonce app-state
  (reagent/atom {:foursquare-access-token nil
                 :checkins []}))

(defn get-state [k & [default]]
  (clojure.core/get @app-state k default))

(defn put! [k v]
  (swap! app-state assoc k v))

;; -------------------------
;; login/logout
(defn foursquare-login! [token]
  (console/debug "Logged in, token:" token)
  (put! :foursquare-access-token token)
  (.replace js/window.location "#/splash"))

(defn foursquare-logout! [token]
  (console/debug "Logged out")
  (put! :foursquare-access-token nil)
  (.replace js/window.location "#/"))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (inspect "State:" (clj->js @app-state))
  (put! :current-page :page1))

(secretary/defroute "/page2" []
                    (put! :current-page :page2))

(secretary/defroute "/splash" []
                    (put! :current-page :splash))

(secretary/defroute #"/foursquare-callback#access_token=([^&]+)" [token]
  (foursquare-login! token))

;; -------------------------
;; kioo templates

(defn onclick [& args]
  (console/log "click!")
  (console/log args))

(defsnippet login-button "templates/splash.html" [:.login] []
  {[:button] (do-> (content "This is the button")
                   (listen :on-click foursquare/redirect-to-foursquare!))})

(deftemplate splash "templates/splash.html" []
  {[:.login] (substitute [login-button])})

;; -------------------------
;; Views

(defmulti page identity)

(defmethod page :page1 [_]
  [:div [:h2 (get-state :text) "Page 1"]
  [:div [:a {:href "#/splash"} "go to splash"]]
  [:div [:a {:href "#/page2"} "go to page 2"]]
  [:div "Token:" (get-state :foursquare-access-token)]
  [:div [:button#login {:on-click foursquare/redirect-to-foursquare!} "Log In"]]
  [:div [:button#logout {:on-click foursquare-logout!} "Log Out"]]
  [:div [:button#check {:on-click #(foursquare/get-checkins! app-state (get-state :foursquare-access-token))} "Check In"]]
  [:div (json-html/edn->hiccup @app-state)]
  ])

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

(defmethod page :splash [_]
  (splash))


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
