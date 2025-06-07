(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [rum "0.12.11" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [ring/ring-defaults "0.3.2"]
                 [markdown-clj "1.12.3"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler app.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
