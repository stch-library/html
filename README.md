# stch.html

DSL for HTML generation. Supports Clojure and ClojureScript.

Based on ideas and some code from [Hoplon](https://github.com/tailrecursion/hoplon).

## Installation

Add the following to your project dependencies:

```clojure
[stch-library/html "0.1.2"]
```

## API Documentation

http://stch-library.github.io/html

Note: This library uses [stch.schema](https://github.com/stch-library/schema). Please refer to that project page for more information regarding type annotations and their meaning.

## How to use

The following examples are all in Clojure.

```clojure
(use 'stch.html)

(defhtml main [users]
  (html5)
  (html
    (head
      (title "My Page"))
    (body
      (section
        (header
          (h1 "Users"))
        (ul :id "users"
          (for [x users]
            (li x))))
      (footer "Copyright 2014"))))

(main ["Billy" "Bobby"])
; "<!DOCTYPE html><html><head><title>My Page</title></head><body><section><header><h1>Users</h1></header><ul id=\"users\"><li>Billy</li><li>Bobby</li></ul></section><footer>Copyright 2014</footer></body></html>"

(deffrag user [id first-name]
  (div :id id first-name))

(user 1 "Billy")
; (#stch.html.Element{:tag "div", :attrs {:id 1}, :children ["Billy"]})
```

The first thing you'll notice is that we're calling fns instead of creating vectors of keywords.  This has a number of advantages.  First, the code itself is less cluttered and looks more like plain html.  Second, we can compose html in really cool ways.

Child node appending:

```clojure
(def users
  (ul
    (li "Billy")
    (li "Bobby")))

(->html users)
; "<ul><li>Billy</li><li>Bobby</li></ul>"
```

Let's append an li element to our ul.

```clojure
(-> (users (li "Joey"))
    ->html)
; "<ul><li>Billy</li><li>Bobby</li><li>Joey</li></ul>"
```

Attribute appending:

```clojure
(def page-title
  (h1 :class "big" "My Page Title"))

(->html page-title)
; "<h1 class=\"big\">My Page Title</h1>"
```

Let's add an id and one more class to our h1.

```clojure
(->html (page-title :id "my-page" :class "blue"))
; "<h1 class=\"big blue\" id=\"my-page\">My Page Title</h1>"
```

This turns out to be really useful when you want to create fns that return generic html (think form elements like select) and add a specific class without having to pass a map of attributes up front.

Let's take a look at an example.

```clojure
(use 'stch.html.form)

(def states [["CA" "California"]
             ["FL" "Florida"]])

(def sel
  (->select "states" states))

(->html sel)
; "<select name=\"states\"><option value=\"CA\">California</option><option value=\"FL\">Florida</option></select>"

(->html (sel :class "us-states"))
; "<select name=\"states\" class=\"us-states\"><option value=\"CA\">California</option><option value=\"FL\">Florida</option></select>"

(-> (sel :class "us-states"
      (option :value "NY" "New York"))
    ->html)
; "<select name=\"states\" class=\"us-states\"><option value=\"CA\">California</option><option value=\"FL\">Florida</option><option value=\"NY\">New York</option></select>"
```

### Here are the ways that you can pass args to an element fn.

No attributes, no children.

```clojure
(div)
; #stch.html.Element{:tag "div", :attrs {}, :children []}
```

Keyword attributes.

```clojure
(div :class "big" :id "my-div")
; #stch.html.Element{:tag "div", :attrs {:class "big", :id "my-div"}, :children []}
```

Map of attributes, where the keys are keywords and the values are any value that can be converted into a string.

```clojure
(div {:class "big" :id "my-div"})
; #stch.html.Element{:tag "div", :attrs {:class "big", :id "my-div"}, :children []}
```

No attributes, with children.

```clojure
(ul
  (li)
  (li))
; #stch.html.Element{:tag "ul", :attrs {}, :children [#stch.html.Element{:tag "li", :attrs {}, :children []} #stch.html.Element{:tag "li", :attrs {}, :children []}]}
```

Attributes and children.

```clojure
(ul :class "user-list"
  (li :class "user" "Billy")
  (li :class "user" "Bobby"))
; #stch.html.Element{:tag "ul", :attrs {:class "user-list"}, :children [#stch.html.Element{:tag "li", :attrs {:class "user"}, :children ["Billy"]} #stch.html.Element{:tag "li", :attrs {:class "user"}, :children ["Bobby"]}]}
```

Element functions return an stch.html.Element record.  It's best to keep your markup in this form as long as possible.  When you're ready to convert to a string, use the ->html fn.

```clojure
(def user-list
  (ul :class "user-list"
    (li :class "user" "Billy")
    (li :class "user" "Bobby")))

(->html user-list)
; "<ul class=\"user-list\"><li class=\"user\">Billy</li><li class=\"user\">Bobby</li></ul>"
```

### class attribute

The class attribute is special in that you can pass a String, Set, or Sequential type (vector, list, etc.) to it.  Let's take a look at some examples.

```clojure
(->html (div :class "big bold"))
; "<div class=\"big bold\"></div>"

(->html (div :class #{"big" "bold"}))
; "<div class=\"big bold\"></div>"

(->html (div :class ["big" "bold"]))
; "<div class=\"big bold\"></div>"
```

### Macros

There are two macros that can be used to define functions that contains html elements.

```clojure
(defhtml user [name age]
  (div :class "user-name" name)
  (div :class "user-age" age))

(user "Billy" 35)
; "<div class=\"user-name\">Billy</div><div class=\"user-age\">35</div>"
```

defhtml automatically wraps the fn body in a call to ->html.

```clojure
(deffrag user [name age]
  (div :class "user-name" name)
  (div :class "user-age" age))

(user "Billy" 35)
; (#stch.html.Element{:tag "div", :attrs {:class "user-name"}, :children ["Billy"]} #stch.html.Element{:tag "div", :attrs {:class "user-age"}, :children [35]})
```

deffrag, on the other hand, does not call ->html. It does wrap the fn body in a list.

### Escaping

Text nodes are automatically escaped for security. If you need to render a string without having it escaped, wrap it in a call to raw.

```clojure
; Escaped
(->html "<script></script>")
; "&lt;script&gt;&lt;/script&gt;"

; Unescaped
(->html (raw "<script></script>"))
; "<script></script>"
```

### Form helpers

There are a few form helper functions.

```clojure
(def states [["CA" "California"]
             ["FL" "Florida"]])

(->html (->options states))
; "<option value=\"CA\">California</option><option value=\"FL\">Florida</option>"

(->html (->select "states" states "CA"))
; "<select name=\"states\"><option selected=\"selected\" value=\"CA\">California</option><option value=\"FL\">Florida</option></select>"

(->html (->radio "states" states "CA"))
; "<input checked=\"checked\" type=\"radio\" name=\"states\" value=\"CA\" id=\"states-0\"><label class=\"form-radio-label\" for=\"states-0\">California</label><input type=\"radio\" name=\"states\" value=\"FL\" id=\"states-1\"><label class=\"form-radio-label\" for=\"states-1\">Florida</label>"

(->html (->checkbox "states" states "CA"))
; "<input checked=\"checked\" type=\"checkbox\" name=\"states\" value=\"CA\" id=\"states-0\"><label class=\"form-checkbox-label\" for=\"states-0\">California</label><input type=\"checkbox\" name=\"states\" value=\"FL\" id=\"states-1\"><label class=\"form-checkbox-label\" for=\"states-1\">Florida</label>"
```

### Doctypes

Finally there are some fns for generating common doctypes.

```clojure
(html5)
; #stch.html.Doctype{:declaration "html"}
(html4-strict)
; #stch.html.Doctype{:declaration "HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n\"http://www.w3.org/TR/html4/strict.dtd\""}
(html4-trans)
; #stch.html.Doctype{:declaration "HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n\"http://www.w3.org/TR/html4/loose.dtd\""}
```

## Unit-tests

Run "lein spec"












