(ns arachne.pedestal-assets.dsl
 (:require [arachne.core.config :as cfg]
           [arachne.core.util :as util]
           [arachne.core.config.script :as script :refer [defdsl]]
           [arachne.core.config.specs :as ccs]
           [clojure.spec.alpha :as s]
           [arachne.error :as e :refer [deferror error]]
           [clojure.string :as str]
           [arachne.http.dsl :as http-dsl]
           [arachne.assets.dsl :as assets-dsl]
           [arachne.pedestal.dsl :as ped-dsl]
           [arachne.core.dsl :as core]))

(s/def ::index? boolean?)

(defdsl interceptor-component
  "Define a component which is a consumer in the asset pipeline, and also a Pedestal interceptor
   that serves resources in its most recent fileset.

   Arguments are:

      - options (optional) - A map (or kwargs) of additional options.

   Currently supported options are:

      - :index? - Sets to true if requests to a directory name should attempt to resolve an
        index.html file within the directory.

   Returns the entity ID of the newly-defined component.

   Note that this form only creates the interceptor component. You must still install it as an
   interceptor on the server using `arachne.pedestal.dsl/interceptor`, and wire it to an asset
   pipeline using `arachne.assets.dsl/pipeline`.

   Note that files will always be found within the fileset by their full url path: this component
   does not (yet) relativize paths to the route to which it is attached. As such, the behavior of
   this component will be most predictable if it is added directly to the root server."
  (s/cat :opts (util/keys** :opt-un [::index?]))
  [& opts]
  (let [tid (cfg/tempid)
        entity (util/mkeep {:db/id tid
                            :arachne.pedestal-assets.fileset-interceptor/index? (:index? (second (:opts &args)))
                            :arachne.component/constructor :arachne.pedestal-assets/fileset-interceptor})]
    (script/transact [entity] tid)))

(defdsl interceptor
  "Define an asset interceptor component and install it in the Pedestal routing structure in a single step.

   This is functionally identical to calling:

       (arachne.pedestal.dsl/interceptor (arachne.pedestal-assets/asset-interceptor))

   Arguments are:

       - options (optional) - A map (or kwargs) of additional options.

   Currently supported options are:

       - :priority - the priority relative to other interceptors defined at the same path. If omitted,
         defaults to the lexical order of the config script
       - :index? - Sets to true if requests to a directory name should attempt to resolve an
         index.html file within the directory.

   Always installs to the server root. Returns the eid of the interceptor component."
  (s/cat :opts (util/keys** :opt-un [::ped-dsl/priority ::index?]))
  [& opts]
  (let [eid (interceptor-component (second (:opts &args)))]
    (apply ped-dsl/interceptor eid opts)
    eid))
