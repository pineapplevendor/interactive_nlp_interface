(ns ^:figwheel-no-load interactive-nlp-interface.dev
  (:require
    [interactive-nlp-interface.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
