;Copyright 2016 Adriano Peluso <catonano@gmail.com> Converting the Arpa Puglia spreadsheets into a .ttl file.

;This file is part of the OpenArpa Project. It is subject to the license terms in the COPYING file found in the top-level directory of this distribution. No part of the OpenArpa Project, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYING file

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

(ns open-arpa.systems
  (:require 
   [com.stuartsierra.component :as component]
   (system.components     
    [repl-server :refer [new-repl-server]]
    )  
   [environ.core :refer [env]]))

(defn dev-system []
  (component/system-map ))
