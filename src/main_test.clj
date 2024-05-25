(ns main-test
  (:require [io.pedestal.test :as test]
            [io.pedestal.log :as l]
            [clojure.edn :as edn]
            [clojure.repl :as repl]
            [main :as main]))

(comment
  (repl/doc edn/read))
(comment
  (deref main/server)
  (test/response-for (:io.pedestal.http/service-fn (deref main/server))
                     :get
                     "/todo")
  (dissoc *1 :body)
  (l/info :test "test")
  (l/info :error *e)
  (l/info :star-one *1)
  (l/info :star-two *2)
  (l/info :star-three *3)
  (dissoc (test/response-for (:io.pedestal.http/service-fn (deref main/server))
                             :delete
                             "/todo")
          :body)
  (dissoc (test/response-for (:io.pedestal.http/service-fn (deref main/server))
                             :get
                             "/no-such-route")
          :body)
  (repl/doc edn/read-string)
  (repl/doc edn/read)
  (:body (test/response-for (:io.pedestal.http/service-fn (deref main/server))
                            :get
                            "/todo/abcdef/12345")))

