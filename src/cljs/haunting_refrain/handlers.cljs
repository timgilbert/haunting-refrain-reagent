(ns haunting-refrain.handlers
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler
                                   path
                                   register-sub 
                                   dispatch 
                                   subscribe]]))

;; Set up all event handlers
; cribbing from https://github.com/Day8/re-frame/blob/master/examples/simple/src/simpleexample/core.cljs

(defn register-all-handlers! []
  (register-handler
    :initialize
    (fn 
      [db _]
      (merge db initial-state))))

(defn init! [initial-state]
  (register-all-handlers!)
  (dispatch [:initialize initial-state]))
