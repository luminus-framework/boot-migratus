(def project 'luminus/boot-migratus)
(def version "1.0")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE" :scope "provided"]
                            [migratus "1.0.3" :scope "provided"]
                            [boot/core "RELEASE" :scope "test"]])

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

(require '[luminus.boot-migratus :refer [migratus]])
