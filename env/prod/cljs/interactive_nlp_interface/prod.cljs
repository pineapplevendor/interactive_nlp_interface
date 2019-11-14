(ns interactive-nlp-interface.prod
  (:require
    [interactive-nlp-interface.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(def env-host "http://54.83.153.72")

(core/init! env-host)
