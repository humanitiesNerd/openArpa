(ns open-arpa.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(def adige (io/file "resources/files/adige.csv"))
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


(defn file-order [file-contents]
  (let [columns (select-the-nth-row-in-a-csv-file file-contents 3)
        measure-units (select-the-nth-row-in-a-csv-file file-contents 5)
        indices (range (count columns))]
    (sort-by :index (map (fn [index column measurement-unit]
                           {:substance column :measurement-unit measurement-unit :index index})
                         indices columns measure-units ))))

(defn file-as-csv [file]
  (list  (.getName file) (csv/read-csv (io/reader file))))

 (defn files-collection [path]
    (filter
     (fn [thing]
       (.isFile thing))
     (file-seq (io/file path))))

(defn back-to-flat [contents]
  (mapv (fn [el]
          [(:date el) (:substance el) (:measurement el) (:measurement-unit el) (:name el)])
        (mapcat (fn [el] el) contents)))
 
(defn file-as-maps [order file-contents name]
  (defn recur-through-row
    ([row] (recur-through-row (next row)  (row 0)))
    ([row date] (let [new-order (map (fn [index] (conj index {:date date})) (next order))]
                  (map (fn [index item] (assoc index :measurement item :name name)) new-order row))))
  
    (map recur-through-row
         file-contents))


(defn process-file [file]
  (let [as-csv (file-as-csv file)
        name (first as-csv)
        contents (second as-csv)
        order (file-order contents)
        purged (drop 7 contents)
        ;; unita di misura
        ]
    (back-to-flat  (file-as-maps order purged name))
    ))

(defn write-file [contents]
  (with-open [out-file (io/writer (str  "resources/processed-files/result.csv" ))]
    (csv/write-csv out-file
                   contents)))

(defn main []
  (write-file (mapcat process-file (files-collection path)))) 
