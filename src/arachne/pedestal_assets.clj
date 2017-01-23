(ns arachne.pedestal-assets
  (:require [arachne.core.config :as cfg]
            [arachne.pedestal-assets.schema :as schema]
            [arachne.assets :as assets]
            [arachne.assets.pipeline :as pipeline]
            [arachne.fileset :as fs]
            [arachne.http.config :as http-cfg]
            [com.stuartsierra.component :as c]
            [io.pedestal.interceptor]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.helpers :as ih]
            [clojure.core.async :as a :refer [go go-loop >! <! <!! >!!]]
            [clojure.string :as str])
  (:import [java.net URI]))

(defrecord FilesetInterceptor [delegate]
  c/Lifecycle
  (start [this]
    (let [delegate (into (pipeline/fileset-view) this)]
      (assoc this :delegate (c/start delegate))))
  (stop [this]
    (c/stop delegate)
    (dissoc this :delegate))
  i/IntoInterceptor
  (-interceptor [this]
    (i/interceptor
      (let [index? (:arachne.pedestal-assets.fileset-interceptor/index? this)]
        {:enter (fn [ctx]
                  (let [req (:request ctx)
                        resp (assets/ring-response delegate req "/" index?)]
                    (if resp
                      (assoc ctx :response resp)
                      ctx)))}))))

(defn ^:no-doc fileset-interceptor
  "Constructor function for the asset interceptor"
  []
  (->FilesetInterceptor nil))

(defn ^:no-doc schema
  "Return the schema for this module"
  []
  schema/schema)
