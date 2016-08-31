(defproject io.replikativ/gezwitscher "0.2.0-SNAPSHOT"

  :description "Basic wrapper around the twitter4j framework"

  :url "https://github.com/replikativ/gezwitscher"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.5"]
                 [midje "1.6.3"]
                 [org.twitter4j/twitter4j-core "4.0.2"]
                 [org.twitter4j/twitter4j-stream "4.0.2"]])
