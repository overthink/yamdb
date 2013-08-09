(ns yamdb.web
  "Routes, handlers, and web app definition."
  (:require
    [net.cgrand.enlive-html :refer [html-resource emit*]]
    [compojure.core :refer [defroutes GET]]
    [compojure.route :refer [not-found resources]]
    [compojure.handler :refer [site]]))

(defn get-home []
  (apply str (emit* (html-resource "templates/index.html"))))

(defroutes main-routes
  (GET "/" [] (get-home))
  (resources "/") ; serves resources from classpath rooted at /public
  (not-found "Not found"))

(def app
  (-> (site main-routes)))

