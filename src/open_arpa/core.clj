;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This file is part of the OpenArpa Project. It is subject to the license terms in the COPYING file found in the top-level directory of this distribution. No part of the OpenArpa Project, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYING file

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

(ns open-arpa.core
  (:require [open-arpa.process :as op]
            [clojure.java.io :as io]
            )
  )
  


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
  (with-open [out-file (io/writer destination-path)]
    (doseq [line lines] (.write out-file line))))

(defn main []
  (writelines "resources/processed-files/semantic-arpa.ttl"
              (thread op/path)))
;; at the repl:
;; (core/main op/path-test)
