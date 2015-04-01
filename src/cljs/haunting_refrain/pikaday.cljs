(ns haunting-refrain.pikaday
  (:require [haunting-refrain.foursquare :as foursquare]
            [cljs.core.async :refer [put! chan <!]]
            [re-frame.core :refer [subscribe dispatch register-sub]]
            [kioo.core :refer [handle-wrapper]]
            [kioo.reagent :refer [content set-attr do-> substitute listen]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]
            [reagent.core :as reagent]
            [cljsjs.pikaday]
            [cljs-time.core :as cljs-time])
  (:require-macros [kioo.reagent :refer [defsnippet deftemplate]]
                   [reagent.ratom :refer [reaction]]))

;; -------------------------
;; pikaday stuff
;; cf https://github.com/thomasboyt/react-pikaday/blob/master/src/Pikaday.js
;; https://github.com/Day8/re-frame/wiki/Creating-Reagent-Components#form-3-a-class-with-life-cycle-methods

(defn selector
  ""
  [input-attrs options]
  (let []
    (reagent/create-class
      {:component-did-mount
        #(console/warn "component mounted!")
       :display-name "pikaday-component"
       :should-component-update
        (fn [this next-props next-state]
          (console/log "update")
          (not= (.-props this) next-props))
       :render
        (fn [input-attrs options]
          (console/log "render")
          [:input input-attrs])})))

(defn selector3
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
              pik (js/Pikaday. dom-node)]
          ))}))

(defn selector4 [attrs selected-date]
  (let [pik (js/Pikaday.)]
    [:input attrs]))
