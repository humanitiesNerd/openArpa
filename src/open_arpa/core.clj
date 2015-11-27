(ns open-arpa.core
  (:require [open-arpa.process :as op]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv])
  (:import (java.io Writer )))
  


;(def coll  (op/files-collection op/Altamura-test))
;(def coll2 (op/files-collection op/path))

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

(def step4 (mapcat op/as-ttl))


(defn thread [path]
  (sequence
        (comp step1 step2 step3 step4)
        (op/files-collection path)))


(defn writelines [destination-path lines]
  (with-open [out-file (io/writer destination-path :append true)]
    (doseq [line lines] (.write out-file line))))



(defn main []
  (writelines "resources/processed-files/semantic-arpa.ttl"
              (thread op/path)))
;; at the repl:
;; (core/main op/path-test)
