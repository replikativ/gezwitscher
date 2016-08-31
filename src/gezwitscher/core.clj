(ns gezwitscher.core
  (:require [clojure.data.json :as json])
  (:import [twitter4j StatusListener TwitterStream TwitterStreamFactory FilterQuery
            Query TwitterFactory Paging Twitter]
           [twitter4j.conf ConfigurationBuilder Configuration]
           [twitter4j.json DataObjectFactory]))

(defn- build-config
  "Twitter stream configuration"
  ^Configuration [{:keys [consumer-key consumer-secret access-token access-token-secret]}]
  (let [cb (ConfigurationBuilder.)]
    (.setDebugEnabled cb true)
    (.setOAuthConsumerKey cb consumer-key)
    (.setOAuthConsumerSecret cb consumer-secret)
    (.setOAuthAccessToken cb access-token)
    (.setOAuthAccessTokenSecret cb access-token-secret)
    (.setJSONStoreEnabled cb true)
    (.build cb)))


(defn- new-status-listener
  "Stream handler, applies given function to newly retrieved status"
  [func error-func]
  (proxy [StatusListener] []
    (onStatus [^twitter4j.Status status]
      (let [parsed-status (json/read-str (DataObjectFactory/getRawJSON status) :key-fn keyword)]
        (func parsed-status)))
    (onException [^java.lang.Exception e] (error-func e))
    (onDeletionNotice [^twitter4j.StatusDeletionNotice statusDeletionNotice] ())
    (onScrubGeo [userId upToStatusId] ())
    (onTrackLimitationNotice [numberOfLimitedStatuses] ())))


(defn- get-twitter-factory
  "Creates a twitter factory"
  [credentials]
  (let [factory (TwitterFactory. (build-config credentials))]
    (.getInstance factory)))


(defn- get-twitter-stream-factory
  "Creates a twitter stream factory"
  [credentials]
  (let [factory (TwitterStreamFactory. (build-config credentials))]
    (.getInstance factory)))


(defn stream
  "Starts streaming, following given ids, tracking given keywords, handling incoming tweets with provided handler function"
  [credentials follow track handler error-handler]
  (let [filter-query (FilterQuery. 0 (long-array follow) (into-array String track))
        stream (get-twitter-stream-factory credentials)]
    (.addListener stream (new-status-listener handler error-handler))
    (.filter stream filter-query)
    (fn [] (.shutdown stream))))


(defn search
  "Creates a twitter search function given credentials and amount, limited to 100 tweets. Returned function requires a keyword as parameter."
  ;;TODO workaround to obtain more tweets
  [credentials search-string]
  (let [twitter (get-twitter-factory credentials)
        query (Query. search-string)
        result (do (.setCount query 100)
                   (.search twitter query))]
    (map #(json/read-str (DataObjectFactory/getRawJSON %) :key-fn keyword) (.getTweets result))))


(defn timeline
  "Creates a function for twitter timeline fetches, limited to 200 tweets. Returned function requires a user as parameter."
  [credentials user]
  (let [twitter (get-twitter-factory credentials)
        page (Paging. (int 1) (int 300))]
    (map #(json/read-str (DataObjectFactory/getRawJSON %) :key-fn keyword)
         (.getUserTimeline twitter user page))))


(defn status-updates
  [credentials status-string]
  (let [twitter (get-twitter-factory credentials)]
    (json/read-str (DataObjectFactory/getRawJSON (.updateStatus twitter status-string))
                   :key-fn keyword)))





(comment

  (def creds
    {:consumer-key "****"
     :consumer-secret "****"
     :access-token "****"
     :access-token-secret "****"})

)
