(ns gezwitscher.core-test
  #_(:use midje.sweet)
  (:require [clojure.test :refer :all]
            [gezwitscher.core :refer :all]
            [clojure.core.async :refer [chan put! <!! >!! go go-loop]]))





