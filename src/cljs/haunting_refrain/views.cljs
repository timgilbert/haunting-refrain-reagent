(ns haunting-refrain.views
  (:require [haunting-refrain.foursquare :as foursquare]
            [cljs.core.async :refer [put! chan <!]]
            [re-frame.core :refer [subscribe dispatch register-sub]]
            [kioo.core :refer [handle-wrapper]]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]])
  (:require-macros [kioo.reagent :refer [defsnippet deftemplate]]
                   [reagent.ratom :refer [reaction]]))

;; -------------------------
;; kioo templates

(defn onclick [& args]
  (console/log "click!")
  (console/log args))

(defsnippet foursquare-logged-out "templates/splash.html" [:.foursquare-logged-out] []
  {[:button] (listen :on-click #(dispatch [:open-login-window "#/foursquare-login"]))})

(defsnippet foursquare-logged-in "templates/splash.html" [:.foursquare-logged-in] []
  {[:button] (listen :on-click #(dispatch [:foursquare-logout]))})

(defsnippet date-selector "templates/splash.html" [:.date-selector] []
  {[:input] (listen :on-click #(console/log "hmm"))})

(deftemplate splash "templates/splash.html" []
  {[:.foursquare-logged-in]  (substitute (foursquare-logged-in))
   [:.foursquare-logged-out] (substitute (foursquare-logged-out))})

(defsnippet date-selector "templates/splash.html" [:.date-selector] []
  {[:input] (listen :on-click #(console/log "hmm"))})

(deftemplate foursquare-login "templates/foursquare-login.html" []
  {[:button.foursquare-login] (listen :on-click #(console/log "hmm"))
   [:a.cancel] (listen :on-click #(dispatch [:close-window]))})

(deftemplate playlist "templates/playlist.html" []
  {})

(defn shell 
  "This is a top-level component which listens for changes to :current-page and 
  then dispatches to the apropriate page-level component."
  []
  (let [page-ratom (subscribe [:go-to-page])
        page-map {:splash splash, :playlist playlist}]
    (fn []
      (let [value @page-ratom]
        (console/log "page-ratom:" value)
        ;(assert (some? (page-map @page-ratom)))
        (get page-map value splash)))))

(defn page-query
  [db [sid cid]]
  (console/log "sid:" (name sid) "cid:" cid)
  (console/log "result:" (:current-page @db))
  (inspect @db)
  (reaction (:current-page @db)))

(register-sub 
  :go-to-page
  page-query)

;(register-sub 
;  :go-to-page
;  (fn [db _]
;    (reaction (:current-page @db))))
