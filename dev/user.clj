(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [open-arpa.systems :refer [dev-system]]
            [open-arpa.core :as op]
            [open-arpa.dictionaries :refer [pollutants]]))

(reloaded.repl/set-init! dev-system)


;; op/Altamura-test

(def coll (op/files-collection op/path-test))

(def step1 (mapcat (comp
                 op/line-numbers
                 op/added-file-order
                 op/splitted-file
                 op/ingested-file)
                coll)) 

(def step2 (map op/parsed-datetime step1))
