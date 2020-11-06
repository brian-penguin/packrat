# Packrat

### ClojureScript
Clojurescript is a almost identical to clojure lisp dialect that focuses on compiling to JS.

## The goal:
- Play a decent amount of pathfinder and one of the things I'm perpetually annoyed by is the loot management system we have for our campaign. It's basically one guys hand scratched notes that we hope he remembers to keep track of. So Today we're going to dip our toes into a clojure script application that will keep track of our party loot and gold.

## Getting Started:
In the past few project I've only used either Selmer <Link needed> or Hiccup <Link Needed> to generate static HTML pages that render content or have stuck with purely json apis in this we're going to try out a neat lil tool called [ShadowCljs](https://shadow-cljs.org/) Which comes with the promise of "providing everything you need to compile your ClojureScript code with a focus on simplicity and ease of use". Thheller does a great breakdown of what this tool is and isn't in [this post](https://code.thheller.com/blog/shadow-cljs/2019/03/01/what-shadow-cljs-is-and-isnt.html)

### Installation and Setup:
Since I want to have a repeatable build and output I'm going to try my best to keep the dependencies the same across my multiple workstations. So lets start by creating a `package.json` file with an empty `{}` in it at the root of our project directory.
Then we can run some npm commands
```
$ npm install shadow-cljs
```
This will eventually let us run our ShadowCljs commands via `npx` which is build in with npm. Alternatively you could install shadow-cljs package globally using `-g`
```
$ npx shadow-cljs watch :app

  - or if installed globally -

$ shadow-cljs watch :app
```

Then we will setup our basic app structure:
```
.
├── public
│   ├── index.html
│   └── scripts
├── shadow-cljs.edn
└── src
    └── web
        └── packrat
            └── core.cljs
```
Here I'm using a src path of `src/web/packrat/core.cljs` instead of the usual `src/core.cljs` to avoid any namespacing collisions I might have later. This will give us the nice and pretty namespace `(ns web.packrat.core)` for our app. If you want to read more about why this is ShadowCljs goes into more detail [here](https://shadow-cljs.github.io/docs/UsersGuide.html#_the_classpath)

We're also going to want to add our basis for our app inside `public/index.html`. This is going to be semi-standard boiler plate for most apps.
```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
  </head>
  <body>
    <div id="app"></div>
    <script src="scripts/core.js"></script>
  </body>
</html>
```
Inside we've added a link to our output JS file inside our public scripts directory and an "app" div that we will use as the entrypoint or the first peice of code to run for our application.

Since we're working with a browser app we're going to start with their example file in `shadow-cljs.edn` and make a few modifications
```
{:source-paths ["src"] ; where you put source files
 :dev-http {8000 "public"}
 :nrepl {:port 55555}
 :dependencies [[reagent "1.0.0-alpha2"]] ; ClojureScript dependencies (we're using reagent which is a clojurescript wrapper around react)
        ; "app" is the build-id, in running "shadow-cljs compile app"
 :builds {:app {:target :browser ; compile code that loads in a browser
                :output-dir "public/scripts"
                :asset-path "/scripts" ; assets loaded from index.html are based on path "/scripts"
                :modules {:core {:init-fn web.packrat.core/run}}}}} ; Tells shadow where to find the "startup" function
```

Lets step through this
1. `:source_paths` - Tells ShadowCljs where too look for our application code
1.  `:dev-http` - Will specify what port we want to run our code on locally while we work. In this case our url will look like `http://localhost:8000/`. This isn't strictly needed and as reloading should still work with the `watch` command and is more for convenience
1. `:nrepl` - This is a way of specifying a port to use for our repl connection. Locally it will allow me to send code from my editor and have it evaluated as part of my development workflow
1. `dependencies` - Here we specify our clojure dependencies. In this case we are using the 1.0 alpha version of reagent, a clojurescript wrapper around React.
1. `builds` - This tells ShadowCljs what to build, where to put the output JS code, where the index.html file is going to try and source our files from (this is the path relative to the index.html file), and where the "entrypoint" function to the the app is located. In this case it will be in `src/web/packrat/core.cljs` main function.

There are a lot of configuration options I haven't delved into here. There are ways to do asset caching, pull in static resources, add modules from npm JS land, and a few ways to add lifecyle hooks to the compiler to have different things happen. The ShadowCljs user guide goes indepth on these.

## Run a Thing!
Now that we've got everything in place we want to make sure it works!

Lets take a quick example and add it to our core.cljs file!
```cljs
(ns web.packrat.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn bold-greeting [message]
  [:h1 [:strong message]])

(defn simple-example-component []
  [:div
   [bold-greeting "Hello World"]
   [:p "Nice to see you"]])

(def app-dom-root
  (js/document.getElementById "app"))

(defn ^:export run []
  (js/console.log "Running!")
  (rdom/render [simple-example-component] app-dom-root))
```

In our namespace we are requiring the reagent.core and reagent dom so we can generate our html into the dom. We then define a method that returns our bold-greeting component.This we use in our simple-example-component to say "Hello World"!

At the bottom of this file we see a function called `run` this is what we pointed to in our `shadow-cljs.edn` config file for the module of our app. In it we are going to log that we are running and then render our componet!

Now lets run our watch command to start up the application and file watching
```
$ npx shadow-cljs watch app
```

If we visit `http://localhost:8000` we should be greeted by our very bold "Hello World".

## Making Changes

Alright! We've got our build up and running so lets make a change to our bold-greeting and see what happens.

```clj
(defn bold-greeting [message]
  [:h1
   [:span {:style {:color "orange"}} message]])
```

And Voila! Now we have an orange and less bold greeting. Switching back to our browser you may notice nothing has changed. The watch command has reloaded but nothing's changed on the web page. We can manually refresh the web page but personally it would be a huge bummer having to reload the page manually with every change in development. So lets add one more thing to make it a nice and smooth experience locally.

We can add to our `core.cljs` file this function:
```clj
(defn ^:dev/after-load start []
  (js/console.log "Starting!")
  (rdom/render [simple-example-component] app-dom-root))
```

Look familiar? It should it's almost exactly the same at the run function. This is one of those lifecycle hooks I mentioned earlier. This hook tells the ui to re-render our component after our code is reloaded. This way both the code that's running in our repl and on our server will match the ui! For more information on what's going on with the cljs code reloading checkout [this post](https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html) by thheller After adding that in lets save and make another change.

```clj
(defn bold-greeting [message]
  [:h1
   [:span {:style {:color "blue"}} message]])
```

This time "Hello World" should automatically turn Blue without any refreshing required!
