(ns open-arpa.process
  (:require
            [clojure.java.io :as io]
            [clojure.data.csv :as csv] 
            [pathetic.core :as paths]
            [open-arpa.dictionaries :as dicts :refer [pollutants stations]]
            [clj-time.core :as t]
            [clj-time.format :as f]
           
            ))


(def centraline (io/file "resources/centraline.csv"))
(def ASM (io/file "resources/new_layout/ASM/2005/BARI Asm 2005.csv"))
(def Giorgi (io/file "resources/new_layout/Giorgiloro/2006/LECCE Surbo 2006.csv"))
(def Cerrate (io/file "resources/new_layout/S. Maria Cerrate/2008/Cerrate.csv"))
(def Mandorli (io/file "resources/new_layout/via dei Mandorli/2013/liceo-scientifico.csv"))
(def CapDiPorto (io/file "resources/new_layout/Capitaneria di Porto/2007/FOGGIA CapDiporto 2007.csv"))

(def Altamura-test (io/file "resources/new_layout-test-data/Altamura/2010/BARI Altamura 2010.csv"))
(def path "resources/new_layout")
(def path-test "resources/new_layout-test-data")
(def det-path "resources/processed-files")


(defn select-the-nth-row-in-a-csv-file [file index]
  (nth file (- index 1)))

(defn file-order [file-contents pollutants]
  (let [columns (next (select-the-nth-row-in-a-csv-file file-contents 3))
        ;measure-units (select-the-nth-row-in-a-csv-file file-contents 5)
        indices (range (count columns))]
    (sort-by :index (map (fn [index column]
                           (let [substance (pollutants column)
                                 ]
                             {:substance substance :measurement-unit (dicts/measurement-units substance) :index index})
                           )
                         indices columns))))

(defn file-contents [as-csv]
  (drop 8 (second as-csv)))

(defn file-body [rows]
  (drop 8 rows))

(defn file-headers [rows]
  (take 8 rows))

(defn ingested-file [file]
         {:file-name (.getName file) :file file :rows  (csv/read-csv (io/reader file))})

(defn splitted-file [map]
  (let [rows (map :rows)
        headers (file-headers rows)
        body (file-body rows)]
    (dissoc
     (assoc map :file-headers headers :file-body body)
     :rows)))


(defn added-file-order [current-map]
  (dissoc (assoc current-map :order (file-order (:file-headers current-map) pollutants)) :file-headers))


(defn produce-stations [file]
  (let [contents (csv/read-csv (io/reader file))]
    (reduce conj (map (fn [row] {(row 3) [(row 6) (row 7)]})  contents))))

(defn coordinates [current-map]
  (let [stations (produce-stations centraline)]          
       (if-let [coords (stations (current-map :station))]
         (assoc current-map
                :lat (coords 0)
                :lon (coords 1))
         (assoc current-map
                :lat ""
                :lon ""))))

(defn new-extracted-station-name [file]
  (let [path (paths/up-dir (paths/up-dir (paths/parse-path file)))
        length (count path)
        index (- length 1)]
    (path index)
  ))

(defn station [current-map]
  (assoc current-map :station (new-extracted-station-name (:file current-map))))


(defn line-numbers [current-map]
  (defn per-row [row number]
    {:file-name (:file-name current-map)
     :file (:file current-map)
     :order (:order current-map)
     :line-number number
     :row row
     :station (:station current-map)
     :lat (:lat current-map)
     :lon (:lon current-map)})
  (let [rows (:file-body current-map)
        numbers (range 1 (+ (count rows) 1 ))]
    (map per-row rows numbers)))

 (defn files-collection [path]
    (filter
     (fn [thing]
       (.isFile thing))
     (file-seq (io/file path))))

(defn back-to-flat [current-map]
  [
   (:datetime current-map)
   (:substance current-map)
   (:measurement current-map)
   (:measurement-unit current-map)
   (:station current-map)
   (:lat current-map)
   (:lon current-map)])

(defn extract-datetime [source-datetime]
  (let [multiparser (f/formatter (t/default-time-zone)
                                 "dd/MM/YYYY HH:mm" "dd/MM/YYYY HH.mm" "dd/MM/YYYY HH.mm.ss" "YYYY-MM-dd HH:mm:ss")]
    ;; the file S. Maria Cerrate/2008/Cerrate.csv has some lines with the first format. I don't know how many more files have the same format
    (f/unparse-local multiparser (f/parse-local multiparser source-datetime))))


(defn parsed-datetime [current-map]
  (let [datetime (get-in current-map [:row 0])]
  
    (try
     
      (update-in 
       (assoc current-map :datetime (extract-datetime datetime))
       [:row]
       (comp vec next))
     
      (catch Exception e
        (println (str "questo datetime \n" ((:row current-map) 0) 
                      "\n non e' parsabile nel file "
                      (.getPath (:file current-map))
                      "\nalla riga "
                      (:line-number current-map) "\n"
                      (.getMessage e)))
        (assoc (update-in current-map [:row] (comp vec next) ) :datetime nil)))))

(defn new-order [current-map]
  (defn update-order [general-order]
    (map (fn [item]
           (assoc item :datetime (:datetime current-map)))
         general-order))
  (dissoc
   (update-in current-map [:order] update-order)
   :datetime))

(defn row-map [current-map]
  (let [row (:row current-map)
        file-order (:order current-map)
        station (:station current-map)
        lat (:lat current-map)
        lon (:lon current-map)]
    (map
     (fn [index item]
       (assoc index
              :measurement item
              :station station
              :lat lat
              :lon lon ))
     file-order
     row)))


(defn empty-cells-filtered-out [current-map]
  (> (count (:measurement current-map)) 0))

