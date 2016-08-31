(ns gezwitscher.core-test
  #_(:use midje.sweet)
  (:require [clojure.test :refer :all]
            [gezwitscher.core :refer :all]
            [clojure.core.async :refer [chan put! <!! >!! go go-loop]]))

(defn random-word [max-length]
  (->> (repeatedly #(rand-nth "0123456789abcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ"))
      (take (rand-int max-length))
      (apply str)))


;; TODO
(deftest twitter-roundtrips
  (testing "Twitter roundtrips."
    (let [creds (-> "resources/credentials.edn" slurp read-string)
          [in out] [] #_(gezwitscher creds)
          track ["clojure" "prolog"]
          follow [146070339]
          word (random-word 100)]
      (>!! in {:topic :timeline :user (first follow)})
      (is (= (:topic (<!! out)) :timeline))
      (>!! in {:topic :search :text "clojure"})
      (is (= (:topic (<!! out)) :search))
      (>!! in {:topic :update-status :text word})
      (is (= (-> out <!! :status :text) word)))))
