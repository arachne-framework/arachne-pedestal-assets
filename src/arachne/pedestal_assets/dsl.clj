(ns arachne.pedestal-assets.dsl
 (:require [arachne.core.config :as cfg]
           [arachne.core.util :as util]
           [arachne.core.config.script :as script :refer [defdsl]]
           [arachne.core.config.specs :as ccs]
           [clojure.spec :as s]
           [arachne.error :as e :refer [deferror error]]
           [clojure.string :as str]
           [arachne.http.dsl :as http-dsl]
           [arachne.assets.dsl :as assets-dsl]
           [arachne.pedestal.dsl :as ped-dsl]
           [arachne.core.dsl :as core]))

(defn- transact-interceptor
  "Transact an interceptor to the context config, returning its eid. Arachne ID may be nil."
  [arachne-id index]
  (let [tid (cfg/tempid)
        entity (util/mkeep {:db/id tid
                            :arachne/id arachne-id
                            :arachne.pedestal-assets.fileset-interceptor/index? (boolean index)
                            :arachne.component/constructor :arachne.pedestal-assets/fileset-interceptor})]
    (script/transact [entity] tid)))

(s/def ::index? boolean?)

(defdsl interceptor
  "Define an interceptor that is also an asset pipeline Consumer. Serves assets from the
   associated pipeline as HTTP resources.

   Arguments are:

   - arachne id (optional) - the Arachne ID of the interceptor component
   - options (optional) - A map (or kwargs) of additional options.

   Currently supported options are:

   - :priority - the priority relative to other interceptors defined at the same path. If omitted,
   defaults to the lexical order of the config script
   - :index? - Sets to true if requests to a directory name should attempt to resolve an
   index.html file within the directory.

   Note that the asset interceptor should always be installed to the server root; attachment to
   other points in the routing table is (currently) not supported. This function should not be
   called inside a `http/context`."
  (s/cat
    :arachne-id (s/? ::core/arachne-id)
    :opts (util/keys** :opt-un [::ped-dsl/priority ::index?]))
  [<arachne-id> & opts]
  (let [priority (-> &args :opts second :priority)
        args ["/"
              (transact-interceptor (:arachne-id &args)
                                    (:index? (second (:opts &args))))
              (when priority
                {:priority priority})]
        args (filter identity args)]
    (apply ped-dsl/interceptor args)))


(comment

  ;;;;; or

  (aa/input-dir :my/files "input/files")

  (ped/server :my/server 8080

    (ped-assets/interceptor )

    (ped/interceptor "/" )

    )

  (aa/pipeline [:my/files :my/server-assets])



  )
