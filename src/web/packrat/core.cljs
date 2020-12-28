(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1
   [:span.text-3xl.text-pink-500 message]])

(defn header []
  [:div.bg-pink-400.p-2.font-black
   [:span.text-white.text-3xl.m-4 "Packrat"]])

(def items
  [{:id 1
    :name "A magic bag of sorts"
    :description "Who can say what this bag may hold!"}
   {:id 2
    :name "Big Sword"
    :description "No, it's like really big"}
   {:id 3
    :name "Too Many hats, but like all together"
    :description "I'm not sure I'm qualified to judge too many hats"}])

(defn items-html [item]
  [:div.object-center.m-2.p-1.border-2.border-purple-500.rounded.p-2
   [:div.text-purple-600.text-lg.underline.p-1 (:name item)]
   [:div.m-1.text-sm (:description item)]])

(defn current-items-section []
  [:div.container.mx-auto.px-4.max-w-prose
   [:div.text-5xl.m-8.text-center.font-black "Current Inventory"]
   (map items-html items)])

(defn html []
  [:div.main
   [header]
   [current-items-section]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export ^:dev/after-load run []
  (js/console.log "Run Called!")
  (rdom/render [html] app-dom-root))
