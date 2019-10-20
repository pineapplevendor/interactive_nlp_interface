(ns ^:figwheel-no-load interactive-nlp-interface.dev
  (:require
    [interactive-nlp-interface.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(def get-relations-path "http://localhost:3000/api/get-relations")

(core/init! get-relations-path)
