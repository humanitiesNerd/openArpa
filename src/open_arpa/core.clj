(ns open-arpa.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(comment {"NMHC" "NOx" "PRESS." "RAD.GLOB." "DV" "RADSG." "VVP" "H2S" "RADST" "Benzene" "V.V." "CH4" "UMID" "NO2" "VV" "RADS" "D.V." "PM2.5" "SIGMA" "TEMP" "RADSG" "PRESS" "NOX" "Pioggia" "RADGLOB" "TEMP." "O3" "PM10 B" "PRESSIONE" "UMID." "RADSN" "NO" "Toluene" "DVG" "SO2" "IPA TOT" "TEMP EST" "E-BENZENE" "BENZENE" "PM10" "UMR" "UMR." "CO" "NOxapi" "PM10 biora" "PIOGGIA" "TOLUENE" "UMIDITA E" "DVP" "O-XYLENE" "MP-XYLENE" "NO2api"}
)

(def adige (io/file "resources/files/adige.csv"))
(def talsano (io/file "resources/files/talsano.csv"))
(def grottaglie (io/file "resources/files/grottaglie.csv"))
(def path "resources/files")
(def det-path "resources/processed-files")


(defn select-the-third-row-in-a-csv-file [file]
  (second (next file)))

(defn third-rows [files]
  (map select-the-third-row-in-a-csv-file files))

(defn unique-columns [third-rows-collection]
"goes through all the files, select the third row in each file and creates a set of all the unique acronyms used there. Such set is gonna be be the set of columns in the final order. With the set of files of 2013 this is the output

{\"NMHC" "NOx" "PRESS." "RAD.GLOB." "DV" "RADSG." "VVP" "H2S" "RADST" "Benzene" "V.V." "CH4" "UMID" "NO2" "VV" "RADS" "D.V." "PM2.5" "SIGMA" "TEMP" "RADSG" "PRESS" "NOX" "Pioggia" "RADGLOB" "TEMP." "O3" "PM10 B" "PRESSIONE" "UMID." "RADSN" "NO" "Toluene" "DVG" "SO2" "IPA TOT" "TEMP EST" "E-BENZENE" "BENZENE" "PM10" "UMR" "UMR." "CO" "NOxapi" "PM10 biora" "PIOGGIA" "TOLUENE" "UMIDITA E" "DVP" "O-XYLENE" "MP-XYLENE" "NO2api}\""
(cons "Parm" (into () (set (mapcat next third-rows-collection)))))


(defn new-order [columns-set]
  (let [final-indices (range (count columns-set))]
    (sort-by :value  (map (fn [column index]
                            {:value column :index-new index})
                          columns-set final-indices))))

(defn file-old-order [file-contents]
  (let [columns (select-the-third-row-in-a-csv-file file-contents)
        indices (range (count columns))]
    (sort-by :value (map (fn [index column] {:value column :index-old index}) indices columns  ))))

(defn file-reordering-structure [new-order order-per-file]
  (sort-by :index-old <
           (map first (filter (fn [sigh] (not (empty? sigh)) )
                              (map (fn [new-thing]
                                     (filter
                                      (fn [thing] (not (nil? thing)))
                                      (map (fn [old-thing] (if (= (:value new-thing) (:value old-thing))
                                                             (conj new-thing old-thing)))
                                           order-per-file)))
                                   new-order)))))


(defn file-as-csv [file]
  (list  (.getName file) (csv/read-csv (io/reader file))))

 (defn files-collection [path]
    (filter
     (fn [thing]
       (.isFile thing))
     (file-seq (io/file path))))

(def final-order
  (new-order
   (unique-columns
    (third-rows
     (map second (map file-as-csv (files-collection path)))))))

(defn back-to-flat [contents]
  (mapv (fn [row]
         (mapv (fn [item]
                 (:value item))
          row))
   contents))

(defn sort-rows [rows]
  (defn sort-row [row]
    (into [] (sort-by :index-new row)))
  (map sort-row
       rows))

(defn file-as-maps [reordering-structure file-contents]
  (mapv (fn [row]
         (mapv (fn [item sort-map]
                (assoc {} :value item :index-old (:index-old sort-map) :index-new (:index-new sort-map) ))
              row reordering-structure))
   file-contents))


(defn process-file [file final-order]
  (let [primo-passo (file-as-csv file)
        name (first primo-passo)
        contents (second primo-passo)
        old-order (file-old-order contents)
        ]
    (back-to-flat (sort-rows (file-as-maps
                              (file-reordering-structure final-order old-order)
                              contents)))))


(defn write-file [file]
  (with-open [out-file (io/writer (str  "resources/processed-files/" (.getName file)))]
    (csv/write-csv out-file
                   (process-file file final-order)))
 )

(defn process-files [path]
  (let [files (files-collection path)
        files-names (map (fn [my-file] (.getName my-file)) files)
        as-csv (map (fn [file] (file-as-csv file)) files)
        processed (map (fn [csv-file] (process-file csv-file final-order)) as-csv)
        ]
    (map (fn [contents file]
           (write-file file contents))
         processed files-names)
    ))


  
(defn main []
  (dorun (map (fn [file] (write-file file)) (files-collection path) )))
