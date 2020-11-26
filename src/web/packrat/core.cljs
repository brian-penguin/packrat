(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1
   [:span.text-3xl.text-pink-500 message]])

(defn header []
  [:div.bg-pink-400
   [:span.text-white.text-3xl "Packrat"]])

(defn simple-example-component []
  [:div
   [bold-greeting "Hello World"]
   [:p "Nice to see you"]])

(defn html []
  [:div.main
   [header]
   [simple-example-component]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export ^:dev/after-load run []
  (js/console.log "Run Called!")
  (rdom/render [html] app-dom-root))
