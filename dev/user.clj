(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [open-arpa.systems :refer [dev-system]]
            [open-arpa.core :as op]
            [open-arpa.dictionaries :refer [pollutants]]))

(reloaded.repl/set-init! dev-system)


;; op/Altamura-test

(def collection (mapv op/ingested-file (op/files-collection op/path-test)))


(def process
  (into []

        
             (mapcat
              (comp
               op/line-numbers
               op/added-file-order
               op/splitted-file))
             
             collection))


;;(def process (into [] handle collection))
