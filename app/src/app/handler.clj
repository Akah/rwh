(ns app.handler
  (:import (java.sql Timestamp))
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [rum.core :as r]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [resource-response]]
            [app.fileparser :refer [generate-files]]))

(r/defc head []
  [:<>
   [:title "rwhite.dev"]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:meta {:name "description" :content "Personal website: rwhite.dev"}]
   [:link {:rel "icon" :href "/images/favicon.png"}]
   [:link {:rel "stylesheet" :href "/style/style.css"}]])

(r/defc header []
  [:header
   [:div "rwhite.dev"]
   [:nav
    [:ul
     [:li
      [:a {:href "/"} "Home"]]
     [:li
      [:a {:href "/blog"} "Blog"]]
     ;; [:li
     ;;  [:a {:href "/portfolio"} "Portfolio"]]
     ;; [:li
     ;;  [:a {:href "/contact"} "Contact"]]
     ]]])

(r/defc footer []
  [:footer
   "Copyright © 2025 Robin White | Source available @ "
   [:a {:href "https://github.com/Akah/rwh"
        :target "_blank"}
    "github.com/akah/rwh"]])

(r/defc page [x]
  [:<>
   [:head (head)]
   (header)
   [:main x]
   (footer)])

(r/defc article [tags title date link]
  [:li {:class "blog-item"}
   [:article {:class "blog-teaser"}
    [:span {:class "blog-tags"}
     (map (fn [tag] [:a (str \# tag)]) tags)]
    [:a {:class "blog-title" :href link} title]
    [:span {:class "blog-date"} date]]])

(defn home []
  (reduce str [(r/render-static-markup
                [:<>
                 [:head (head)]
                 (header)])
               (str "<main class=\"blog\">"
                    (slurp (str "out/html/blogs/hello-world.html"))
                    "</main>")
               (r/render-html (footer))]))

(defn blog []
  (reduce str [(r/render-static-markup
                [:<>
                 [:head (head)]
                 [:link {:rel "icon" :href "/images/favicon.png"}]
                 [:link {:rel "stylesheet" :href "/style/style.css"}]
                 (header)])
               (str "<main class=\"blog\">"
                    (slurp "out/html/blogs-list.html")
                    "</main>")
               (r/render-html (footer))]))

(r/defc portfolio []
  [:<> (page "portfolio")])

(r/defc contact []
  [:<> (page "contact")])

(defn blog-article [name]
  (reduce str [(r/render-static-markup
                [:<>
                 [:head
                  (head)
                  [:link {:rel "stylesheet" :href "/style/dracula.css"}]
                  [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js"}]
                  [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/languages/lisp.min.js"}]
                  [:script "hljs.highlightAll();"]]
                 (header)])
               (str "<main class=\"blog\">"
                    (slurp (str "out/html/blogs/" name ".html"))
                    "</main>")
               (r/render-html (footer))]))

(defn blog-tag [tag]
  (reduce str [(r/render-static-markup
                [:<>
                 [:head (head)]
                 (header)])
               (str "<main class=\"blog\">"
                    (slurp (str "out/html/tags/" tag ".html"))
                    "</main>")
               (r/render-html (footer))]))

(defroutes app-routes
  (route/resources "public")
  (GET "/" []
       (home))
  (GET "/blog" []
       (blog))
  (GET ["/blog/tag/:path" :path #".*"] [path & params]
       (blog-tag path))
  (GET ["/blog/:path" :path #".*"] [path & params]
       (blog-article path))
  (GET "/portfolio" []
       (r/render-static-markup (portfolio)))
  (GET "/contact" []
       (r/render-static-markup (contact)))
  ;; (GET "/test" {params :query-params}
  ;;      (testy))
  (route/not-found "404: Page Found"))

(generate-files)

(def app
  (wrap-defaults app-routes site-defaults))
