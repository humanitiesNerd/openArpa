;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, with an exception added by the author; either version 3 of the License, or (at your option) any later version.

;A copy of such license (in its version 3.0) with the aforementioned exception can be found at the root of this distribution in the COPYING file

;This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.


(defproject open-arpa "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.danielsz/system "0.1.4"]
                 [environ "1.0.0"]
                 [pathetic "0.5.1"]
                 [clj-time "0.11.0"]]
  :profiles {:dev {:source-paths ["dev"]}} 
                 )
