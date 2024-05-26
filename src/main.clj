(ns main
  (:require [io.pedestal.http :as http]
            [clojure.repl :as repl]
            [io.pedestal.http.route :as route]))

(defonce database (atom {}))

(comment
  (repl/doc gensym)
  (gensym "2a")
  (deref database))

(def db-interceptor
  {:name :database-interceptor,
   :enter (fn [context] (update context :request assoc :database @database)),
   :leave (fn [context]
            (if-let [[op & args] (:tx-data context)]
              (do (apply swap! database op args)
                  (assoc-in context [:request :database] @database))
              context))})

(defn make-list [nm] {:name nm, :items {}})

(defn make-list-item [nm] {:name nm, :done? false})

(defn response
  [status body & {:as headers}]
  {:status status, :body body, :headers headers})

(def ok (partial response 200))
(def created (partial response 201))
(def accepted (partial response 202))

(def list-create
  {:name :list-create,
   :enter
     (fn [context]
       (let [nm (get-in context [:request :query-params :name] "Unamed List")
             new-list (make-list nm)
             db-id (str (gensym "1"))
             url (route/url-for :list-view :params {:list-id db-id})]
         (assoc context
           :response (created new-list "Location" url)
           :tx-data [assoc db-id new-list])))})

(def echo
  {:name :echo,
   :enter (fn [context]
            (let [_request (:request context)
                  response (ok context)]
              (assoc context :response response)))})

(def routes
  (route/expand-routes
    #{["/todo" :post [db-interceptor list-create]]
      ["/todo" :get echo :route-name :list-query-form]
      ["/todo/:list-id" :get echo :route-name :list-view]
      ["/todo/:list-id" :post echo :route-name :list-item-create]
      ["/todo/:list-id/:item-id" :get echo :route-name :list-item-view]
      ["/todo/:list-id/:item-id" :put echo :route-name :list-item-update]
      ["/todo/:list-id/:item-id" :delete echo :route-name :list-item-delete]}))

(def service-map {::http/routes routes, ::http/type :jetty, ::http/port 8890})

;; For interactive development
(defonce server (atom nil))

(defn start-dev
  []
  (reset! server (http/start (http/create-server (assoc service-map
                                                   ::http/join? false)))))

(defn stop-dev [] (http/stop @server))

(defn restart [] (stop-dev) (start-dev))

(comment
  (start-dev)
  (stop-dev)
  (restart))

