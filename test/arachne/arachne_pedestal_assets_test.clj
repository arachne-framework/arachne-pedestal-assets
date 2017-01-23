(ns arachne.arachne-pedestal-assets-test
  (:require [clojure.test :refer :all]
            [arachne.core :as core]
            [arachne.core.config :as cfg]
            [arachne.arachne-pedestal-assets :as module]
            [com.stuartsierra.component :as c]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [arachne.core.dsl :as a]
            [arachne.arachne-pedestal-assets.dsl :as dsl])
  (:import [java.io FileNotFoundException]))

(deftest math
  (is (= 1 1)))
