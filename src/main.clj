(ns main
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test]))

(defn response
  [status body & {:as headers}]
  {:status status, :body body, :headers headers})

(def ok (partial response 200))
(def created (partial response 201))
(def accepted (partial response 202))

