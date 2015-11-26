
(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [open-arpa.systems :refer [dev-system]]
            [open-arpa.process :as op]
            [open-arpa.core :as core]
            [open-arpa.dictionaries :refer [pollutants]]))

(reloaded.repl/set-init! dev-system)


;; op/Altamura-test

(def coll (op/files-collection op/Altamura-test))

(def step1 (mapcat (comp
                    op/line-numbers
                    
                    op/station
                    op/added-file-order
                    op/splitted-file
                    op/ingested-file)
                   )) 

(def step2 (mapcat (comp
                 op/row-map
                 op/new-order
                 op/parsed-datetime)))

(def step3 (filter op/empty-cells-filtered-out))

(def step4 (map op/as-ttl))

(def thread (into [] (comp step1 step2 step3 step4)  coll))


;(def thread (first (sequence (comp step1 step2 step3) coll) ))


(comment
(def step4 "a")
)
;; at the repl:
;; (core/main op/path-test)
