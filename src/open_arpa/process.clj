;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This file is part of the OpenArpa Project. It is subject to the license terms in the COPYING file found in the top-level directory of this distribution. No part of the OpenArpa Project, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYING file.


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
(def dest-path "resources/processed-files")


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
     
     
     })
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
   
   
   ])

(defn extract-datetime [source-datetime]
  (let [multiparser (f/formatter (t/default-time-zone)
                                 "dd/MM/YYYY HH:mm" "dd/MM/YYYY HH.mm" "dd/MM/YYYY HH.mm.ss" "YYYY-MM-dd HH:mm:ss")]
    ;(f/unparse-local multiparser
                     (f/parse-local multiparser source-datetime))
    ;)
  )


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
        ]
    (map
     (fn [index item]
       (assoc index
              :measurement item
              :station station
              
              
              ))
     file-order
     row)))


(defn empty-cells-filtered-out [current-map]
  (> (count (:measurement current-map)) 0))


(def reconciliated-values
  {"NO2" {
          :dbpedia "Nitrogen_dioxide"
          :dbpedia-it "Diossido_di_azoto"}
   "CO" {
         :dbpedia "Carbon_monoxide"
         :dbpedia-it "Monossido_di_carbonio"}
   "HC" {
         :dbpedia "Hydrocarbon"
         :dbpedia-it "Idrocarburi"}
   "NO" {
         :dbpedia "Nitric_oxide"
         :dbpedia-it "Monossido_di_azoto" }
   "NOx" {
          :dbpedia "NOx"
          :dbpedia-it "NOx"}
   "O3" {
         :dbpedia "Ozone"
         :dbpedia-it "Ozono" }
   "PM10" {
           :dbpedia "Particulates"
           :dbpedia-it "PM10" }
   "SO2" {
          :dbpedia "Sulfur_dioxide"
          :dbpedia-it "Diossido_di_zolfo"}
   "CH4" {
          :openarpa "CH4"}
   "H2S" {
          :openarpa "H2S"}
   "NMHC" {
           :openarpa "NMHC"}
   })

(defn observedProperties [substance]
  (let [stuff (reconciliated-values substance)]
    (apply str
           (interpose ", "
                      (mapv
                       (fn [item]
                         (str (name (first item)) ":" (second item)))  stuff)))))

;(def multiparser (f/formatter-local "dd/MM/YYYY HH:mm"))
(def id-formatter (f/formatter-local "ddMMYYYYHHmmss"))
(def rdf-formatter (f/formatter-local "dd/MM/YYYY HH:mm:ss"))
(def daytime-formatter (f/formatter-local "HH:mm:ss"))

;(defn new-parsed-datetime [datetime]
;  (f/parse-local multiparser datetime))

(defn unparsed-datetime [formatter datetime]
  (f/unparse-local formatter datetime))

(defn year [datetime]
  (t/year datetime))

(defn month [datetime]
  (t/month datetime))

(defn day [datetime]
  (t/day datetime))

(defn lines [path]
  (csv/read-csv (io/reader (io/file path))))


(defn headers []
  (vector "@prefix openarpa-obs: <http://openpuglia.org/lod/observation/> .
@prefix openarpa-sens: <http://openpuglia.org/lod/sensor/> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix dbpedia: <http://dbpedia.org/resource/> .
@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> .
@prefix time: <http://www.w3.org/2006/time#> .
@prefix dbpedia-it: <http://it.dbpedia.org/resource/> .
@prefix basic: <http://def.seegrid.csiro.au/isotc211/iso19103/2005/basic#> .
@prefix foaf: <http://xmlns.com/foaf/0.1/> .
\n\n\n
"))


(defn as-ttl [{:keys [datetime substance measurement measurement-unit station] :as record}]
  (vector
   (str "\n" "openarpa-obs:" station substance
        (unparsed-datetime id-formatter datetime)
        " a " "ssn:Observation" " ;" "\n")
   (str "\t" "ssn:observationResultTime "
        "\"" (unparsed-datetime rdf-formatter datetime) "\""
        "^^xsd:dateTime ;" "\n")
   (str "\t" "ssn:isProducedBy openarpa-sens:" station " ;" "\n")
   (str "\t" "time:month " "\"" (month datetime) "\"" "^^xsd:gMonth ;" "\n")
   (str "\t" "time:year " "\"" (year datetime) "\"" "^^xsd:gYear ;" "\n")
   (str "\t" "time:day " "\"" (month datetime) "\"" "^^xsd:gDay ;" "\n")
   (str "\t")
   (observedProperties substance)
   (str "\n")
   (str "\t" "ssn:hasValue " "\"" measurement  "\"" " ;\n")
   (str "\t" "time:inDateTime " "\"" (unparsed-datetime daytime-formatter datetime) "\"" "^^xsd:time ;" "\n")
   (str "\t" "basic:uom " "\"" measurement-unit "\"")
   (str " .\n")

   
   (str  "\n" "openarpa-sens:" station  " ;\n"
	"\t" "ssn:hasOutput " "openarpa-obs:" station substance (unparsed-datetime id-formatter datetime) " .\n")
   
   )
 )
