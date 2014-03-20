(ns stch.html.form
  "Generate HTML form elements."
  (:use [stch.util :only [indexed]]
        [stch html schema])
  (:require [clojure.core :as core])
  (:import stch.html.Element))

(def ValueDisplayPairs [(Pair String String)])
(def AttrsOrVal (U Map String))
(def InputLabelPairs [(Pair Element Element)])

(defn' ->options :- [Element]
  "Converts a sequence of value/display pairs to option
  elements. Optionally, provide the selected option."
  ([options :- ValueDisplayPairs]
   (->options options nil))
  ([options :- ValueDisplayPairs
    selected :- (Option String)]
   (for [[value display] options
         :let [attrs {:value value}]]
     (option (if (= value selected)
               (assoc attrs :selected "selected")
               attrs)
             display))))

(defn' ->select :- Element
  "Returns an HTML select element given an element
  name, a sequence of value/display pairs, an optional map
  of attributes (e.g., name, class), and an optional selected
  value."
  ([name :- Named, options :- ValueDisplayPairs]
   (->select name options nil))
  ([name :- Named
    options :- ValueDisplayPairs
    selected :- (Option String)]
   (select {:name (core/name name)}
           (->options options selected))))

(defn' ->radio :- InputLabelPairs
  "Returns HTML radio elements given an element
  name, a sequence of value/display pairs, an optional
  map of attributes (e.g., name, class), and an optional
  checked value."
  ([name :- Named, values :- ValueDisplayPairs]
   (->radio name values nil))
  ([name :- Named
    values :- ValueDisplayPairs
    checked :- (Option String)]
   (for [[index [value display]]
         (indexed values)
         :let [name (core/name name)
               id (str name "-" index)
               input-attrs {:type "radio"
                            :name name
                            :value (core/name value)
                            :id id}]]
     [(input
       (if (= value checked)
         (assoc input-attrs :checked "checked")
         input-attrs))
      (label {:class "form-radio-label"
              :for id}
             display)])))

(defn' ->checkbox :- InputLabelPairs
  "Returns HTML checkbox elements given an element
  name, a sequence of value/display pairs, an optional
  map of attributes (e.g., name, class), and an optional
  checked value."
  ([name :- Named, values :- ValueDisplayPairs]
   (->checkbox name values nil))
  ([name :- Named
    values :- ValueDisplayPairs
    checked :- (Option String)]
   (for [[index [value display]]
         (indexed values)
         :let [name (core/name name)
               id (str name "-" index)
               input-attrs {:type "checkbox"
                            :name name
                            :value (core/name value)
                            :id id}]]
     [(input
       (if (= value checked)
         (assoc input-attrs :checked "checked")
         input-attrs))
      (label {:class "form-checkbox-label"
              :for id}
             display)])))

(defn' capture-image :- Element
  [n :- String]
  (input {:type "file"
          :name n
          :accept "image/*"
          :capture "capture"}))

(defn' capture-video :- Element
  [n :- String]
  (input {:type "file"
          :name n
          :accept "video/*"
          :capture "capture"}))

(defn' capture-audio :- Element
  [n :- String]
  (input {:type "file"
          :name n
          :accept "audio/*"
          :capture "capture"}))





