(ns interactive-nlp-interface.core
    (:require
      [reagent.core :as r]
      [cljs-http.client :as http]
      [cljs.core.async :refer [<! go]]))

(def relations (r/atom []))

(def next-question (r/atom "What do you want to eat?"))

(def meaning-extractor-url "http://localhost:3000/api/")

(def get-relations-path (str meaning-extractor-url "get-relations"))

(def get-next-question-path (str meaning-extractor-url "get-next-question"))

(defn update-relations [text]
  (go (let [response (<! (http/post get-relations-path
                           {:json-params {:text text}
                            :with-credentials? false}))]
        (#(reset! relations (:body response))))))

(defn update-next-question [text]
  (go (let [response (<! (http/post get-next-question-path
                           {:json-params {:text text}
                            :with-credentials? false}))]
        (#(reset! next-question (:question-text (:body response)))))))

(defn handle-input [current-text new-input]
  (reset! current-text new-input)
  (update-relations @current-text)
  (update-next-question @current-text)
  (prn @current-text))

(defn accept-input [current-text]
  [:textarea {:type "text"
           :value @current-text
           :on-change #(handle-input current-text (-> % .-target .-value))}])

(defn display-relation
    [relation]
    (str (:subject relation) "->" (:relation relation) "->" (:object relation)))

(defn display-relations 
    [relations]
    (map display-relation relations))

(defn render-relations
    [relations]
    (str relations)
    [:ul
      (for [r (display-relations relations)]
        ^{:key r} [:li r])])

(defn get-next-question
    [text]
    [:div text])

(defn create-input-area []
  (let [input-text (r/atom "")]
    (fn []
      [:div [accept-input input-text]])))

(defn home-page []
  [:div 
    [:h4 "Input"]
    [create-input-area]
    [:h4 "Extracted Relations"]
    [render-relations @relations]
    [:h4 "Next Question"]
    [get-next-question @next-question]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))

