(ns com.msg.dropbox
  (:require [org.httpkit.client :as http]
            [cheshire.core :refer [parse-string]]
            [plumbing.core :refer :all]
            [ring.util.codec :refer [url-encode]]))

(defn- dropbox-req
  ([access-token endpoint]
     (dropbox-req access-token endpoint false))
  ([access-token endpoint content?]
     (let [root (if content?
                  "api-content"
                  "api")
           json->edn (comp keywordize-map parse-string)
           result (:body
          @(http/get (str "https://" root ".dropbox.com/1" (url-encode endpoint))
                     {:query-params
                      {:access_token access-token}}))]
       (cond-> result
               (not content?) json->edn))))

(defn get-info [access-token]
  (dropbox-req access-token "/account/info"))

(defn metadata
  ([access-token]
     (metadata access-token "/"))
  ([access-token path]
     (dropbox-req access-token (str "/metadata/auto" path))))

(defn get-file [access-token path]
  (dropbox-req access-token (str "/files/auto" path) true))
