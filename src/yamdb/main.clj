(ns yamdb.main
  "Entry point for app. Fns for starting/stopping web server."
  (:require
    [yamdb.web :as web]
    [compojure.core :refer [defroutes GET]]
    [ring.adapter.jetty :refer [run-jetty]]
    [compojure.route :refer [not-found]]
    [compojure.handler :refer [site]])
  (:import
    [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

;;(defroutes main-routes
;;  (GET "/" [] "Hey now")
;;  (not-found "Not found"))
;;
;;(def app
;;  (-> (site main-routes)))

(defonce server (ref nil))

(defn config-server
  "Access to the server instance before it is started.  Returns nil."
  [^Server s]
  (.setStopAtShutdown s true))

(defn start-server
  "If not already running, start the embedded web server and run the given
  (compojure) app.  Does nothing if server already running.

  Usage: (start-server #'my-app)            <-- good for repl
         (start-server #'my-app :join true) <-- good for main"
  [app-var & {:keys [join] :or {join false}}]
  (dosync
    (when-not (ensure server)
      (ref-set server (run-jetty app-var
                                 {:port 3000
                                  :join? join
                                  :configurator config-server})))))

(defn stop-server
  "Initiate orderly shutdown of the server.  Blocks until done.  If already
  shutdown, does nothing."
  []
  (dosync
    (when-let [^Server s (ensure server)]
      (.stop s)
      (ref-set server nil))))

(defn -main [& args]
  ; start the server and block forever
  (start-server :join true)
  (println "bye"))

(comment
  (start-server #'web/app)
  (stop-server)
)

