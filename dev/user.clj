;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This file is part of the OpenArpa Project. It is subject to the license terms in the COPYING file found in the top-level directory of this distribution. No part of the OpenArpa Project, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYING file.


(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [open-arpa.systems :refer [dev-system]]
            [open-arpa.process :as op]
            [open-arpa.core :as core]
            [open-arpa.dictionaries :refer [pollutants]]))

(reloaded.repl/set-init! dev-system)


;; op/Altamura-test

(def coll (op/files-collection op/Altamura-test))

(def big-coll op/path)

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
  (sequence (comp step1 step2 step3 step4)  (op/files-collection path)))


;; at the repl:
;; (core/main op/path-test)
