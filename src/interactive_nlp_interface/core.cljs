(ns interactive-nlp-interface.core
    (:require
      [reagent.core :as r]))

(def submitted-text (r/atom ""))
(def relations (r/atom [{:subject "subject"
                         :relation "relation"
                         :object "object"},
                         {:subject "subject"
                         :relation "relation"
                         :object "object"}]))
(def next-question (r/atom "the-next-question"))

(defn text-input [current-text]
  [:input {:type "text"
           :value @current-text
           :on-change #(reset! current-text (-> % .-target .-value))}])

(defn submit-text [current-text]
  [:input {:type "button" 
           :value "Submit"
           :on-click #(reset! submitted-text @current-text)}])

(defn display-submitted []
  [:div "Submitted: " @submitted-text])

(defn display-relation
    [relation]
    (str (:subject relation) "->" (:relation relation) "->" (:object relation)))

(defn display-relations 
    [relations]
    (map display-relation relations))

(defn render-relations
    [relations]
    [:ul
      (for [dr (display-relations relations)]
        [:li dr])])

(defn get-next-question
    [text]
    [:div text])
        

(defn accept-input []
  (let [input-text (r/atom "")]
    (fn []
      [:div
       [:p "Current Input: " @input-text]
       [:p "Input: " [text-input input-text]]
       [submit-text input-text]])))

(defn home-page []
  [:div 
    [:h4 "Input"]
    [accept-input]
    [display-submitted]
    [:h4 "Extracted Relations"]
    [render-relations [{:subject "I" :relation "like" :object "pizza"},
                        {:subject "he" :relation "eats" :object "cheese"}]]
    [:h4 "Next Question"]
    [get-next-question @next-question]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))

