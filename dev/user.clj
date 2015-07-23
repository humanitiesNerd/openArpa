(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [open-arpa.systems :refer [dev-system]]
            [open-arpa.core :as op]
            [open-arpa.dictionaries :refer [pollutants]]))

(reloaded.repl/set-init! dev-system)

(def step1 (op/ingested-file op/Altamura-test))

(def step2 (op/splitted-file step1))

(def step3 (op/added-file-order step2))

(def step4 (op/line-numbers step3))

(def step5 (map op/parsed-datetime step4))

;;(def process (map op/parse-dates handle))

;; (dorun (println process))
