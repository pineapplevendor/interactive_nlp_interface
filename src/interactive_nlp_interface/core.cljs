(ns interactive-nlp-interface.core
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [dommy.core :as dommy :refer-macros [sel1]]
   [cljs.core.async :refer [<! take! go]])
  (:import 
   [goog.async Debouncer]))

(def get-relations-path (atom "http://localhost:3000/api/get-relations"))

(defonce extracted-relations (r/atom ()))

(defn display-relation [relation]
  (str (:subject relation) " - " (:relation relation) " - " (:object relation)))

(defn display-sentence-relations [sentence-relations]
  [:div (:sentence sentence-relations)
   [:ul (map (fn [relation]
               [:li (display-relation relation)])
             (:relations sentence-relations))]])

(defn display-relations []
  [:ul (map (fn [sentence-relations] 
              [:li (display-sentence-relations sentence-relations)])
            @extracted-relations)])

(defn set-extracted-relations! []
  (r/render [display-relations] (sel1 :#extracted-information)))

(defn get-user-input []
  (.-value (sel1 :#user-input)))

(defn update-extracted-relations! []
  (go (let [response (<! (http/post @get-relations-path
                                    {:json-params {:text (get-user-input)}
                                     :with-credentials? false}))]
        (reset! extracted-relations (:body response)))))

(def update-relations-debouncer (Debouncer. update-extracted-relations! 500))

(defn debounced-update-relations! []
  (.fire update-relations-debouncer))

(dommy/listen! (sel1 :#user-input) :input debounced-update-relations!)

;; -------------------------
;; Initialize app

(defn mount-root []
  (set-extracted-relations!))

(defn init! [relations-path]
  (reset! get-relations-path relations-path)
  (mount-root))

