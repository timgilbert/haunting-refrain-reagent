(ns haunting-refrain.foursquare
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; -------------------------
;; General foursquare settings

(def foursquare-api-version "20141202")

(defn foursquare-api-url [endpoint token]
  (str "https://api.foursquare.com/v2/"
       endpoint
       "?oauth_token=" token
       "&v=" foursquare-api-version))

;; -------------------------
;; Login / logout

; cf https://developer.foursquare.com/overview/auth.html
; https://foursquare.com/developers/app/BAL2VGI3TXOWFI1TGH4O4VIHBLQ4AUC404YYSRRT5OJJEGGL

(def ^:private foursquare-client-id "BAL2VGI3TXOWFI1TGH4O4VIHBLQ4AUC404YYSRRT5OJJEGGL")

(def ^:private redirect-uri "http://localhost:3000/%23%2ffoursquare-callback")

(def ^:private foursquare-redirect-url
  (str "https://foursquare.com/oauth2/authenticate"
       "?client_id=" foursquare-client-id
       "&response_type=token"
       "&redirect_uri=" redirect-uri))

(defn redirect-to-foursquare! []
  "Redirect the browser to foursquare's oauth endpoint. If successful it will
  return to redirect-uri#access_token=XYZZY"
  (set! (.-location js/window) foursquare-redirect-url))

;; -------------------------
;;
(defn get-checkins! [app-state token]
  "Call foursquare's checkin endpoint. When it returns, update :checkins in
  app-state with an ednified version of the result."
  (go (let [url (foursquare-api-url "users/self/checkins" token)
            response (<! (http/get url {:with-credentials? false}))]
        (when (:success response))
        (inspect response)
        (console/debug "response:" response)
        )))