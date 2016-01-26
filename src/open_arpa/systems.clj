;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, with an exception added by the author; either version 3 of the License, or (at your option) any later version.

;A copy of such license (in its version 3.0) with the aforementioned exception can be found at the root of this distribution in the COPYING file

;This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

(ns open-arpa.systems
  (:require 
   [com.stuartsierra.component :as component]
   (system.components     
    [repl-server :refer [new-repl-server]]
    )  
   [environ.core :refer [env]]))

(defn dev-system []
  (component/system-map ))
