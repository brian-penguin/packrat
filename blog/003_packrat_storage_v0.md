# Packrat

## Storage V0

### The Goal
This is our first pass at keeping some state within our app! We want to get our CRUD (Create Read Update Destroy) actions and find a way to store them so our users can do something with them.

### Lets start!
Right now we've got some "items" that we have hardcoded in our `def items`. Instead we are going to use something from the reagent library called an atom. This atom comes from reagent.core and functions similarly to the core clojure [atom](https://clojure.org/reference/atoms). Our reagent/atom will let us store state and have it reactively re-render when the value of the state changes. We can use a quick example from the Reagent readme.

```clojure
(defonce click-count (r/atom 0))

(defn state-ful-with-atom []
  [:div {:on-click #(swap! click-count inc)}
   "I have been clicked " @click-count " times."])
```

Here we store our click-count once as a reagent atom and add a function to the on-click event which will update the click count by one! Each update will re-render the div with the new string and updated click count using the @syntax to dereference our atom. Lets add our atom instead of our hardcoded list.
```clojure
(defonce items
  (r/atom [...]))
```
now we can change our `current-items-section` to map over `@items` instead of `items`

Lets start with my favorite of the CRUD actions, delete!

