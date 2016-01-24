;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, with an exception added by the author; either version 3 of the License, or (at your option) any later version.

;A copy of such license (in its version 3.0) with the aforementioned exception can be found at the root of this distribution in the COPYING file

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.


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
