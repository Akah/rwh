(ns app.fileparser
  (:require [clojure.string :as str]
            [clojure.java.io :refer [make-parents]]
            [rum.core :as r]
            [markdown.core :refer [md-to-html md-to-html-string]]))

(defn get-blog-markdown []
  (filter #(str/ends-with? % ".md")
          (map #(.getPath %)
               (file-seq (clojure.java.io/file "blog/")))))

(defn generate-html [file-in output-dir]
  "convert markdown file to html and return meta-data"
  (let [name (str/replace file-in #"^blog/(.+).md$" "$1")
        file-out (str output-dir name ".html")]
    (println file-in "->" file-out)
    (make-parents file-out)
    (md-to-html file-in file-out :parse-meta? true)))

(defn parse-files [files output-dir]
  (println "parsing files...")
  (map #(generate-html % output-dir) files))

(defn generate-blogs []
  (parse-files (get-blog-markdown) "out/html/blogs/"))

(r/defc article-local [tags title date link]
  [:li {:class "blog-item"}
   [:article {:class "blog-teaser"}
    [:span {:class "blog-tags"}
     (map (fn [tag] [:a {:href (str "/blog/tag/" tag)}
                     (if-not (nil? tag)
                       (str \# tag))])
          (str/split tags #" "))]
    [:a {:class "blog-title" :href link} title]
    [:span {:class "blog-date"} date]]])

(defn blog-entry->article [{:keys [tags title date name]}]
  (article-local
   (first tags)
   (first title)
   (first date)
   (str "/blog/" (first name))))

(defn generate-blogs-list [meta-data]
  (let [articles (map blog-entry->article meta-data)
        html [:main articles]]
    (spit "out/html/blogs-list.html"
          (r/render-static-markup html))))

(defn get-tags-lists [meta-data]
  (mapcat (fn [{:keys [tags name title date]}]
            (let [tag-str (first tags)]
              (map (fn [tag]
                     {:tag tag
                      :name (first name)
                      :title (first title)
                      :date (first date)
                      :tags tag-str})
                   (str/split tag-str #" "))))
          meta-data))

(defn merge-names-by-tag [coll]
  (->> coll
       flatten
       (filter map?)
       (group-by :tag)
       (map (fn [[tag entries]]
              {:tag tag
               :data entries}))))

(defn generate-tags-lists [tags-list]
  (map (fn [{:keys [tag data]}]
         (spit (str "out/html/tags/" tag ".html")
               (r/render-static-markup
                [:<>
                 [:h2 (str "Tag: " tag)]
                 (map (fn [blog]
                        (article-local (get blog :tags)
                                       (get blog :title)
                                       (get blog :date)
                                       (str "/blog/"
                                            (get blog :name))))
                      data)])))
       tags-list))

(defn generate-files []
  (let [md (generate-blogs)
        tags-list (merge-names-by-tag (get-tags-lists md))]
    (generate-tags-lists tags-list)
    (generate-blogs-list md)))
