(ns ^:figwheel-no-load interactive-nlp-interface.dev
  (:require
    [interactive-nlp-interface.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(def env-host "http://localhost:3000")

(core/init! env-host)
