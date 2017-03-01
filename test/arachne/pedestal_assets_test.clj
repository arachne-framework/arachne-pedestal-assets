(ns arachne.pedestal-assets-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [com.stuartsierra.component :as c]
            [arachne.core :as core]
            [arachne.http :as http]
            [arachne.core.config :as cfg]
            [arachne.core.runtime :as rt]
            [arachne.pedestal :as ped]
            [ring.util.response :as ring-resp]
            [clj-http.client :as client]
            [arachne.core.dsl :as a]
            [arachne.http.dsl :as h]
            [arachne.assets.dsl :as aa]
            [arachne.pedestal.dsl :as p]
            [arachne.pedestal-assets.dsl :as pa])
  (:import [java.io FileNotFoundException]))

(defn asset-interceptor-cfg []

  (a/id :test/rt (a/runtime [:test/server]))

  (a/id :test/input (aa/input-dir "test/test-assets"))

  (a/id :test/ai (pa/interceptor-component :index? true))

  (a/id :test/server
    (p/server 8080
      (p/interceptor :test/ai)
      ))

  (aa/pipeline [:test/input :test/ai])

  )

(defn asset-interceptor-cfg-streamlined []

  (a/id :test/rt (a/runtime [:test/server]))

  (a/id :test/input (aa/input-dir "test/test-assets"))

  (a/id :test/server
    (p/server 8080
      (a/id :test/ai (pa/interceptor :index? true))))

  (aa/pipeline [:test/input :test/ai])

  )

(deftest ^:integration asset-interceptor-test
  (let [cfg-a (core/build-config [:org.arachne-framework/pedestal-assets]
                `(asset-interceptor-cfg) true)
        cfg-b (core/build-config [:org.arachne-framework/pedestal-assets]
                `(asset-interceptor-cfg-streamlined) true)]
    (doseq [cfg [cfg-a cfg-b]]
      (let [rt (core/runtime cfg :test/rt)
            rt (c/start rt)]
        (try

          (is (= "<p>index</p>" (slurp "http://localhost:8080")))
          (is (= "<p>index</p>" (slurp "http://localhost:8080/index.html")))
          (is (= "<p>file</p>" (slurp "http://localhost:8080/dir1/file.html")))

          (let [result (try (client/get "http://localhost:8080/no-such-file.html")
                            (catch Exception e (ex-data e)))]
            (is (= 404 (:status result))))


          (finally (c/stop rt)))))))
