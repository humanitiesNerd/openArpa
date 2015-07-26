(ns open-arpa.core
  (:require [open-arpa.process :as op]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))


(defn step1 [coll] (mapcat (comp
                    op/line-numbers
                    op/coordinates
                    op/station
                    op/added-file-order
                    op/splitted-file
                    op/ingested-file)
                   coll)) 

(defn step2 [step1] (mapcat (comp
                 op/row-map
                 op/new-order
                 op/parsed-datetime
                 ) step1))

(defn step3 [step2] (filter op/empty-cells-filtered-out step2))

(defn step4 [step3] (map op/back-to-flat step3))

(defn write-file [contents]
  (with-open [out-file (io/writer (str  "resources/processed-files/result.csv" ))]
    (csv/write-csv out-file
                   contents)))


(defn main [path]
  (write-file (step4 (step3 (step2 (step1 (op/files-collection path)))))))

;; at the repl:
;; (core/main op/path-test)
