(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1
   [:span {:style {:color "blue"}} message]])

(defn simple-example-component []
  [:div
   [bold-greeting "Hello World"]
   [:p "Nice to see you"]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export run []
  (js/console.log "Running!")
  (rdom/render [simple-example-component] app-dom-root))

(defn ^:dev/after-load start []
  (js/console.log "Starting!")
  (rdom/render [simple-example-component] app-dom-root))
