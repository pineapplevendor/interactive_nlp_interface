(ns interactive-nlp-interface.core
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [dommy.core :as dommy :refer-macros [sel1]]
   [cljs.core.async :refer [<! take! go]])
  (:import 
   [goog.async Debouncer]))

(def host (atom "http://localhost:3000"))

(defn get-url [host path]
  (str host path))

(defn get-relations-path []
  (get-url @host "/api/get-relations"))
(defn get-next-in-experiment-path []
  (get-url @host "/api/get-next-sentence-in-experiment/"))
(defn submit-sentence-path []
  (get-url @host "/api/submit-sentence/"))

(def experiment "testing-1")

(defonce extracted-relations (r/atom ()))
(defonce current-benchmark-sentence (r/atom {}))

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

(defn render-extracted-relations! []
  (r/render [display-relations] (sel1 :#extracted-information)))

(defn display-original-sentence []
  [:div (:sentence @current-benchmark-sentence)])

(defn render-original-sentence! []
  (r/render [display-original-sentence] (sel1 :#original-sentence)))

(defn get-user-input []
  (.-value (sel1 :#user-input)))

(defn update-extracted-relations! []
  (go (let [response (<! (http/post (get-relations-path)
                                    {:json-params {:text (get-user-input)}
                                     :with-credentials? false}))]
        (reset! extracted-relations (:body response)))))

(def update-relations-debouncer (Debouncer. update-extracted-relations! 500))

(defn debounced-update-relations! []
  (.fire update-relations-debouncer))

(dommy/listen! (sel1 :#user-input) :input debounced-update-relations!)

(defn update-current-benchmark-sentence! [experiment-id]
  (go (let [response (<! (http/get (str (get-next-in-experiment-path) experiment-id)
                                   {:with-credentials? false}))]
        (reset! current-benchmark-sentence (:body response)))))

(defn clear-text-area! []
  (reset! extracted-relations [])
  (dommy/set-value! (sel1 :#user-input) ""))

(defn prepare-next-sentence! [experiment-id]
  (clear-text-area!)
  (update-current-benchmark-sentence! experiment-id))

(defn submit-sentence! [updated-benchmark-sentence experiment-id]
  (go (<! (http/post (str (submit-sentence-path) experiment-id)
                                    {:with-credentials? false
                                     :json-params updated-benchmark-sentence}))
      (prepare-next-sentence! experiment-id)))

(defn submit-re-written-sentence! []
  (submit-sentence! (assoc @current-benchmark-sentence :sentence (get-user-input))
                    experiment))

(dommy/listen! (sel1 :#submit-button) :click submit-re-written-sentence!)

;; -------------------------
;; Initialize app

(defn mount-root []
  (render-extracted-relations!)
  (render-original-sentence!)
  (prepare-next-sentence! experiment))

(defn init! [env-host]
  (reset! host env-host)
  (mount-root))

