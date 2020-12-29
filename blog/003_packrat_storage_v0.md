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
  (r/atom (sorted-map)))
```
A sorted-map is a map with indexed keys for easy lookup. This should be more memory efficient because we can generate an id for each item and associate new items and dissassociate removed ones by id. We also get the benefit of faster lookups by id instead of iterating through a collection to get to the item we want to update or remove.

For development purposes (and since we're starting with the delete action) it makes sense to have an initial set of data to work with. So lets add one!
We can set our items sorted-map to an initial value.
```clojure
(defonce items
  (r/atom (sorted-map 1 {:id 1 :name "Big hat" :description "really big hat"} 2 {:id 2 :name "Small sword" :description "Not even sure you could call it a sword"})))
```
Now we can change our `current-items-section` function to map over `(val @items)` instead of `items` to use our new sorted-map.
Note our id is both the key and a value inside of our map for each item. This works great for our hardcoded list but how will we know if when we add an item we aren't associating an item to the wrong id or deleting an item. So lets think about that now before we make any more choices about how we structure our data. We know we want to have a series of crud actions and we need to keep track of our collection as we modifiy it. We want to define a function that lets us do each of the crud actions.
```clojure
(defn items (r/atom (sorted-list))) ;; Our collection of items index by ids
(defn delete [id] ...) ;; Given an id, remove from our items
(defn update [id] {:attr1 ...}) ;; assocate our attrs in our item
(defn read [id] ...) ;; Since we are working with a collection we don't really need this as we can destructure our atom and use the values like this (vals @items)
(defn create [name description] ... ) ;; Updates the item with a new id and item map
```
One of the challenges here is the id! When we create a new item how will we know what id to use when we want to update or delete it later? One solution is to keep a second "counter" atom which as an integer and let the create function set the id for the items. Lets see what that could look like
```clojure
(defonce items (r/atom (sorted-map))
;; Keep track of ids of items in use
(defonce counter (r/atom 0))

(defn create [title description]
  (let [id (swap! counter inc)] ;; swap! will increment the counter and return the value as our new id
    (swap! items assoc id {:id id :name title :description description}))) ;; here it will assoc our item into our items atom by id
```
Each time we create a new item we will want to update our id counter so that we don't ever have a conflicting id. This is one of the benefits of clojure's atom. It provides a way to manage shared, synchronous, immutable state. Under the hood it's providing a safe way for us to make sure that if another thread changes the underlying value it will call the swapping function again and garuntees an safe opertation. One of the tricky bits with atoms is that they can be called multiple time if the value changes so we need to be sure our function is free of side effects.

With our new addition we can easily add a delete function which removes our item from a sorted map by dissassociating the id from our items collection
```clojure
(defn delete [id] (swap! items dissoc id))
```

As we grow our application we are likely going to change the data and the requirements for what we need to do. Because of this we should try to minimize the ways we store and add to our itmes collection. Instead of hardcoding values directly to our items atom lets call the create function with hardcoded data when we first initalize our application. This will let us rely on our new crud interface as a consistent api and allow us to change how it performs under the hood. Maybe it needs to call to an external api or store something in the session data but as long as we are using our create function we don't have to worry.

We can make use of an init function to set our data! Something like this
```clojure
(defonce init (do
                (create "A magic bag of sorts" "Who can say what this bag may hold!")
                (create "Big sword" "No, it's like really big!")
                (create "Too many hats" "I'm not sure I'm qualified to say what is too many hats, but it's like a lot")))
```

Awesome! Now we have a way to create and delete in our code but no ui for it. Lets update our function that handles rendering each item to include a small button on the right for deleting data and attach an on-click handler for actually calling our delete api.
```clojure
(defn items-html [item]
  [:div.object-center.m-2.p-1.border-2.border-purple-500.rounded.p-2
   [:div.text-purple-600.text-lg.underline.p-1 (:name item)]
   [:div.m-1.text-sm (:description item)]
   [:div.text-sm.text-right.border-solid [:button.destroy {:on-click #(delete (:id item))} "Delete"]]])
```
The key bit here is this section `{:on-click #(delete (:id item))}` on-click is a way of tying a function to a trigger in our dom. When our button is clicked we call the annonymous function to delete from items our item based on the items id (This is why we are keeping track of ids within our item data). If you've never seen the # syntax before it's a short hand for `(fn [item] (delete (:id item)))`
