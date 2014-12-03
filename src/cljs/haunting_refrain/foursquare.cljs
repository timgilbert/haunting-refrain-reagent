(ns haunting-refrain.foursquare
  (:require [shodan.console :as console :include-macros true]
            [shodan.inspection :refer [inspect]]))

; cf https://developer.foursquare.com/overview/auth.html
; https://foursquare.com/developers/app/BAL2VGI3TXOWFI1TGH4O4VIHBLQ4AUC404YYSRRT5OJJEGGL

;; Login / logout

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

