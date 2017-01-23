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

  (a/runtime :test/rt [:test/server])

  (aa/input-dir :test/input "test/test-assets")

  (p/server :test/server 8080

    (pa/interceptor :test/ai :index? true)

    )

  (aa/pipeline [:test/input :test/ai])

  )

(deftest ^:integration asset-interceptor-test
  (let [cfg (core/build-config [:org.arachne-framework/pedestal-assets]
              `(asset-interceptor-cfg) true)
        rt (core/runtime cfg :test/rt)]
    (let [rt (c/start rt)]
      (try

        (is (= "<p>index</p>" (slurp "http://localhost:8080")))
        (is (= "<p>index</p>" (slurp "http://localhost:8080/index.html")))
        (is (= "<p>file</p>" (slurp "http://localhost:8080/dir1/file.html")))

        (let [result (try (client/get "http://localhost:8080/no-such-file.html")
                          (catch Exception e (ex-data e)))]
          (is (= 404 (:status result))))


        (finally (c/stop rt))))))
