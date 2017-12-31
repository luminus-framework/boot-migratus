(def project 'luminus/boot-migratus)
(def version "1.0.1")

(set-env! :resource-paths #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE" :scope "provided"]
                            [migratus "1.0.3" :scope "provided"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]
                            [boot/core "RELEASE" :scope "test"]])

(require '[adzerk.bootlaces :as bl]
         '[clojure.java.io :as io]
         '[clojure.edn :as edn])

(bl/bootlaces! version)

(task-options!
 pom {:project     project
      :version     version
      :description "Maintain database migrations"
      :url         "https://github.com/luminus-framework/boot-migratus"
      :scm         {:url "https://github.com/luminus-framework/boot-migratus"}
      :license     {"MIT"
                    "https://opensource.org/licenses/MIT"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(defn get-gpg-key-id
  "Gets the gpg key-id based on a gpg.edn file locally or in the home
  directory. It expects a map with a :user-id key."
  []
  (let [home-file (io/file (System/getProperty "user.home") "gpg.edn")
        local-file (io/file "gpg.edn")]
    (if (or (.exists home-file) (.exists local-file))
      (:user-id (edn/read-string (slurp (if (.exists local-file)
                                          local-file
                                          home-file)))))))

(defn- get-creds []
  (mapv #(System/getenv %) ["CLOJARS_USER" "CLOJARS_PASS"]))

(deftask collect-clojars-credentials
  "Collect CLOJARS_USER and CLOJARS_PASS from the user if they're not set.

  Credit to adzerk bootlaces for code."
  []
  (fn [next-handler]
    (fn [fileset]
      (let [[user pass] (get-creds), clojars-creds (atom {})]
        (if (and user pass)
          (swap! clojars-creds assoc :username user :password pass)
          (do (println "CLOJARS_USER and CLOJARS_PASS were not set; please enter your Clojars credentials.")
              (print "Username: ")
              (#(swap! clojars-creds assoc :username %) (read-line))
              (print "Password: ")
              (#(swap! clojars-creds assoc :password %)
               (apply str (.readPassword (System/console))))))
        (merge-env! :repositories [["deploy-clojars" (merge @clojars-creds {:url "https://clojars.org/repo"})]])
        (next-handler fileset)))))

(deftask push-clojars
  "Build project and push it to clojars"
  [f file PATH str "PATH sets the jar file to deploy."
   g gpg-key KEY str "KEY sets the name or key-id used to select the signature key"]
  (let [key-id (if gpg-key
                 gpg-key
                 (get-gpg-key-id))]
    (comp
     (collect-clojars-credentials)
     (push
      :file file
      :gpg-sign true
      :gpg-user-id key-id
      :repo "deploy-clojars"))))

(require '[luminus.boot-migratus :refer [migratus]])
