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
                    op/coordinates
                    op/station
                    op/added-file-order
                    op/splitted-file
                    op/ingested-file)
                   coll)) 

(def step2 (mapcat (comp
                 op/row-map
                 op/parsed-datetime
                 ) step1))

(def step3 (filter op/empty-cells-filtered-out step2))

(def step4 (map op/back-to-flat step3))

