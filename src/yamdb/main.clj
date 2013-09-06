(ns yamdb.main
  "Entry point for app. Fns for starting/stopping web server."
  (:require
    [clojure.tools.nrepl.server :as nrepl]
    [yamdb.web :as web]
    [compojure.core :refer [defroutes GET]]
    [ring.adapter.jetty :refer [run-jetty]]
    [compojure.route :refer [not-found]]
    [compojure.handler :refer [site]])
  (:import
    [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

(defonce nrepl-server (nrepl/start-server :port 7888 :bind "127.0.0.1"))
(defonce web-server (ref nil))

(defn config-web-server
  "Access to the server instance before it is started.  Returns nil."
  [^Server s]
  (.setStopAtShutdown s true))

(defn start-web-server
  "If not already running, start the embedded web server and run the given
  (compojure) app.  Does nothing if server already running.

  Usage: (start-web-server #'my-app)            <-- good for repl
         (start-web-server #'my-app :join true) <-- good for main"
  [app-var & {:keys [join] :or {join false}}]
  (dosync
    (when-not (ensure web-server)
      (ref-set web-server (run-jetty app-var
                                     {:port 3000
                                      :join? join
                                      :configurator config-web-server})))))

(defn stop-web-server
  "Initiate orderly shutdown of the web server.  Blocks until done.  If already
  shutdown, does nothing."
  []
  (dosync
    (when-let [^Server s (ensure web-server)]
      (.stop s)
      (ref-set web-server nil))))

(defn -main [& args]
  ; start the web server and block forever
  (start-web-server web/app :join true)
  (println "bye"))

(comment
  (start-web-server #'web/app)
  (stop-web-server)
)

