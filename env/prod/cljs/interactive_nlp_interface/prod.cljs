(ns interactive-nlp-interface.prod
  (:require
    [interactive-nlp-interface.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(def get-relations-path "http://54.83.153.72/api/get-relations")

(core/init! get-relations-path)
