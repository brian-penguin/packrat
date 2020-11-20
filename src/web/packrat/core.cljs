(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1
   [:span.text-3xl.text-pink-500 message]])

(defn simple-example-component []
  [:div
   [bold-greeting "Hello World"]
   [:p "Nice to see you"]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export ^:dev/after-load run []
  (js/console.log "Run Called!")
  (rdom/render [simple-example-component] app-dom-root))
