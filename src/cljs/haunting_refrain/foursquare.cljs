(ns haunting-refrain.foursquare)

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
    (set! (.-location js/window) foursquare-redirect-url))
