(ns yamdb.web
  "Routes, handlers, and web app definition."
  (:require
    [net.cgrand.enlive-html :as html :refer [html-resource emit* deftemplate defsnippet]]
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


(comment

  (get-home)

  (deftemplate t1 (html-resource "templates/index.html")
    [title]
    [:h1] (html/content title)
    [:title] (html/content (str "title is: " title)))

  (t1 "foo?")


)
