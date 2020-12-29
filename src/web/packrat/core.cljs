(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1
   [:span.text-3xl.text-pink-500 message]])

(defn header []
  [:div.bg-pink-400.p-2.font-black
   [:span.text-white.text-3xl.m-4 "Packrat"]])

;(defonce store
  ;(r/atom {1 {:name "A magic bag of sorts"
              ;:description "Who can say what this bag may hold!"}
           ;2 {:name "Big Sword"
              ;:description "No, it's like really big"}
           ;3 {:name "Too Many hats, but like all together"
              ;:description "I'm not sure I'm qualified to judge too many hats"}}))


(defonce items (r/atom (sorted-map))
;; Keep track of ids of items in use
(defonce counter (r/atom 0))

(defn delete [id] (swap! items dissoc id))

(defn create [title description]
  (let [id (swap! counter inc)]
    (swap! items assoc id {:id id :name title :description description})))

;; This gets called once on "init" and will create some default items for us
(defonce init (do
                (create "A magic bag of sorts" "Who can say what this bag may hold!")
                (create "Big sword" "No, it's like really big!")
                (create "Too many hats" "I'm not sure I'm qualified to say what is too many hats, but it's like a lot")))

(defn items-html [item]
  [:div.object-center.m-2.p-1.border-2.border-purple-500.rounded.p-2
   [:div.text-purple-600.text-lg.underline.p-1 (:name item)]
   [:div.m-1.text-sm (:description item)]
   [:div.text-sm.text-right.border-solid [:button.destroy {:on-click #(delete (:id item))} "Delete"]]])

(defn current-items-section []
  [:div.container.mx-auto.px-4.max-w-prose
   [:div.text-5xl.m-8.text-center.font-black "Current Inventory"]
   ;; Use vals not the id and item so we can use the item for generating HTML and not worry about the id
   (map items-html (vals @items))])

(defn html []
  [:div.main
   [header]
   [current-items-section]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export ^:dev/after-load run []
  (js/console.log "Run Called!")
  (rdom/render [html] app-dom-root))
