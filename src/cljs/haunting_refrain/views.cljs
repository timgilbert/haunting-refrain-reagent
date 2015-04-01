(ns haunting-refrain.views
  (:require [haunting-refrain.foursquare :as foursquare]
            [cljs.core.async :refer [put! chan <!]]
            [re-frame.core :refer [subscribe dispatch register-sub]]
            [kioo.core :refer [handle-wrapper]]
            [kioo.reagent :refer [content set-class remove-class do-> substitute listen]]
            [reagent.core :as reagent]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [cljsjs.pikaday :as pikaday]
            [cljs-time.core :as cljs-time])
  (:require-macros [kioo.reagent :refer [defsnippet deftemplate]]
                   [reagent.ratom :refer [reaction]]))

;; -------------------------
;; pikaday stuff
;; cf https://github.com/thomasboyt/react-pikaday/blob/master/src/Pikaday.js
;; https://github.com/Day8/re-frame/wiki/Creating-Reagent-Components#form-3-a-class-with-life-cycle-methods

(defn pikaday-selector2
  ""
  [input-attrs options]
  (let [_ 2 #_(console/log "aieee")]
    (reagent/create-class
      {:component-did-mount
        #(console/warn "component mounted!")
       :reagent-render
        (fn [input-attrs options]
          (console/log "render")
          [:input input-attrs])})))

(defn pikaday-selector
  ""
  [input-attrs options]
  (let [_ (console/log "aieee")]
    (fn [input-attrs options]
      (console/log "render")
      [:input {:type "text"}])))

(defn- plain-date-selector [attrs selected-date]
  (let []
    (fn [{:keys [id]} selected-date]
      [:input {:id id} ])))

#_(def date-selector
  (with-meta plain-date-selector
    {:component-did-mount
      (fn [this]
        (let [dom-node (reagent/dom-node this)
              pik (Pikaday. dom-node)]
          ))}))

;; -------------------------
;; kioo templates

(defn onclick [evt]
  (console/log "click!")
  (console/log evt))

(defsnippet foursquare-logged-out "templates/splash.html" [:.foursquare-logged-out] 
  [[foursquare-ratom]]
  {[:.foursquare-logged-out] 
     (if (nil? @foursquare-ratom) 
       (remove-class "hidden")
       (set-class "hidden"))
    [:button] (listen :on-click #(dispatch [:redirect-to-foursquare "#/foursquare-login"]))})

(defsnippet foursquare-logged-in "templates/splash.html" [:.foursquare-logged-in] 
  [[foursquare-ratom]]
  {[:.foursquare-logged-in] 
     (if (nil? @foursquare-ratom) 
       (set-class "hidden")
       (remove-class "hidden"))
   [:button] (listen :on-click #(dispatch [:foursquare-logout]))})

(defsnippet date-selector "templates/splash.html" [:.date-selector] []
  {[:input] (listen :on-click #(console/log "hmm"))})

(defsnippet splash "templates/splash.html" [:.main-shell] [foursquare-ratom]
  {[:.foursquare-logged-in]  (substitute (foursquare-logged-in foursquare-ratom))
   [:.foursquare-logged-out] (substitute (foursquare-logged-out foursquare-ratom))
   ; Should parameterize these better
   [:.start :input]          (substitute (pikaday-selector 
                                           {:id "startdate"} 
                                           {:on-change #(dispatch [:new-date :start %])}))
   [:.end :input]            (substitute (pikaday-selector 
                                           {:id "enddate"} 
                                           {:on-change #(dispatch [:new-date :end %])}))})

(defsnippet date-selector2 "templates/splash.html" [:.date-selector] []
  {[:input] (listen :on-click #(console/log "hmm"))})

(defsnippet playlist "templates/playlist.html" [:.main-shell] []
  {})

(defsnippet about "templates/about.html" [:.main-shell] [] {})

(defsnippet error "templates/error.html" [:.main-shell] [page] {})

(deftemplate shell-template "templates/shell.html" [shell-content & args]
  {[:.main-shell] (content (apply shell-content args))})

(defn shell 
  "This is a top-level component which listens for changes to :current-page and 
  then dispatches to the apropriate page-level component."
  []
  (let [page-ratom (subscribe [:go-to-page])
        foursquare-ratom (subscribe [:foursquare-token])
        page-map {:splash [splash foursquare-ratom] 
                  :playlist [playlist] 
                  :about [about]}]
    (fn []
      (let [page-fn (get page-map @page-ratom [error])
            foursquare-token @foursquare-ratom]
        (console/log "page-ratom:" @page-ratom)
        (console/log "page-fn:" (clj->js page-fn))
        ;(assert (some? (page-map @page-ratom)))
        (shell-template (first page-fn) (rest page-fn))))))

(defn page-query
  [db [sid cid]]
  (console/log "sid:" (name sid) "cid:" cid)
  (console/log "result:" (get-in @db [:navigation :current-page]))
  (inspect @db "@db")
  (reaction (get-in @db [:navigation :current-page])))

(defn foursquare-token-query
  [db [sid cid]]
  (reaction (get-in @db [:foursquare :token])))

(defn init! []
  (register-sub 
    :go-to-page
    page-query)

  (register-sub 
    :foursquare-token
    foursquare-token-query))

;(register-sub 
;  :go-to-page
;  (fn [db _]
;    (reaction (:current-page @db))))
