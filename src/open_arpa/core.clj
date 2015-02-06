(ns open-arpa.core
  
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [open-arpa.dictionaries :as dicts :refer [pollutants stations]]
            ))

(def centraline (io/file "resources/centraline.csv"))
(def arnesano (io/file "resources/files/ARNESANO_2013.csv"))
(def andria (io/file "resources/files/andria.csv"))
(def adige (io/file "resources/files/test_data/adige_test.csv"))
(def talsano (io/file "resources/files/talsano.csv"))
(def grottaglie (io/file "resources/files/grottaglie.csv"))
(def path "resources/files")
(def det-path "resources/processed-files")


(defn select-the-nth-row-in-a-csv-file [file index]
  (nth file (- index 1)))

(defn third-rows [files]
  (map select-the-nth-row-in-a-csv-file files (repeat 3)))

(defn fifth-rows [files]
  (map select-the-nth-row-in-a-csv-file files (repeat 5)))


(defn file-order [file-contents pollutants]
  (let [columns (select-the-nth-row-in-a-csv-file file-contents 3)
        measure-units (select-the-nth-row-in-a-csv-file file-contents 5)
        indices (range (count columns))]
    (sort-by :index (map (fn [index column measurement-unit]
                           {:substance (pollutants column) :measurement-unit measurement-unit :index index})
                         indices columns measure-units ))))

(defn file-as-csv [file]
  (list  (.getName file) (csv/read-csv (io/reader file))))

 (defn files-collection [path]
    (filter
     (fn [thing]
       (.isFile thing))
     (file-seq (io/file path))))

(defn file-contents [file]
  (drop 7 (second (file-as-csv file))))

(defn back-to-flat [contents]
  (mapv (fn [el]
          [(:date el) (:substance el) (:measurement el) (:measurement-unit el) (:station el) (:lat el) (:lon el)])
         contents))
 
(defn file-as-maps [order file-contents station]
  (defn recur-through-row
    ([row] (recur-through-row (next row)  (row 0)))
    ([row date] (let [new-order (map (fn [index] (conj index {:date date})) (next order))]
                  (remove nil? (map (fn [index item]
                                         (if (> (count item) 0)
                                           (assoc index :measurement item :station station)))
                                       new-order
                                       row)))))
  
    (map recur-through-row
         file-contents))

(defn station-name [file-contents]
   (second  (select-the-nth-row-in-a-csv-file file-contents 2))) 

(defn insert-coordinates [file-contents stations]
  (map (fn [item]

         (if-let [coords (stations (item :station))]
           (assoc item
                  :lat (coords 0)
                  :lon (coords 1))
           (assoc item
                  :lat "0000"
                  :lon "0000"))
   
         )
       (mapcat (fn [el] el)  file-contents)
  ))

(defn produce-stations [file]
  (let [contents (csv/read-csv (io/reader file))]
    (reduce conj (map (fn [row] {(row 3) [(row 6) (row 7)]})  contents))))

(defn process-file [file stations pollutants]
  (let [as-csv (file-as-csv file)
        ;name (first as-csv)
        contents (second as-csv)
        station (dicts/stations (station-name contents))        
        order (file-order contents pollutants)
        purged (drop 7 contents)
        ;; unita di misura
        ]
    (back-to-flat (insert-coordinates (file-as-maps order purged station) (produce-stations centraline)))))

(defn write-file [contents]
  (with-open [out-file (io/writer (str  "resources/processed-files/result.csv" ))]
    (csv/write-csv out-file
                   contents)))

(defn main []
  (write-file (mapcat process-file
                      (files-collection path)
                      (repeatedly (fn [] dicts/stations))
                      (repeatedly (fn [] dicts/pollutants)) ))) 



(defn stations-names []
  (apply sorted-set (map second (map station-name (files-collection path)))))
  
(defn pollutant-name [file]
  (let [file-contents (second (file-as-csv file))]
    (into [] (rest (select-the-nth-row-in-a-csv-file file-contents 3)))))

(defn pollutants-names []
  (apply sorted-set (mapcat pollutant-name (files-collection path))))



