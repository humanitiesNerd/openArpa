;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This file is part of the OpenArpa Project. It is subject to the license terms in the COPYING file found in the top-level directory of this distribution. No part of the OpenArpa Project, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYING file.


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
