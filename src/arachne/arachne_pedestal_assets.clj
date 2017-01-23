(ns arachne.arachne-pedestal-assets
  (:require [arachne.core.config :as cfg]
            [arachne.arachne-pedestal-assets.schema :as schema]))

(defn schema
  "Return the schema for this module"
  []
  schema/schema)

(defn configure
  "Configure phase for this module"
  [cfg]
  cfg)
