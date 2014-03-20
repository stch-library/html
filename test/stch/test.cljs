(ns stch.test
  (:use [stch.html :only [section header h1
                          ul li a]])
  (:use-macros [stch.html.macros :only [defhtml]])
  (:require [stch.dom :as dom]))

(defhtml links [link-pairs]
  (section
    (header
      (h1 "Links"))
    (ul :id "links"
      (for [[href display] link-pairs]
        (li :class "link"
          (a :href href display))))))

(defn ^:export run
  "Render HTML to browser."
  []
  (let [html
        (links [["http://clojure.org" "Clojure"]
                ["http://clojuredocs.org" "ClojureDocs"]])]
    (dom/set-html js/document.body html)))
