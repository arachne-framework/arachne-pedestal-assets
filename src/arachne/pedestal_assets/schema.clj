(ns arachne.pedestal-assets.schema
  (:require [arachne.core.config.model :as o]))

(def schema
  (concat
    (o/type :arachne.pedestal-assets/FilesetInterceptor [:arachne.pedestal/Interceptor :arachne.assets/Consumer]
      "An Consumer/Interceptor that serves the resources in a fileset"
      (o/attr :arachne.pedestal-assets.fileset-interceptor/index? :one-or-none :boolean
        "Boolean value indicating whether requests to a directory path should be handled by a /index.html file in that directory."))))
