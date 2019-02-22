(ns interactive-nlp-interface.prod
  (:require
    [interactive-nlp-interface.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
