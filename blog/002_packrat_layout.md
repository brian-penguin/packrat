# Packrat

### Layout and Styles

### The Goal
 We want to setup a quick layout that lets us act like power users for our inventory management. To do this we're going to take a look at loading some custom fonts, using a prebuilt spiritual successor to bootstrap called tailwind css, and some clojure-land specific markup syntax.

### Tailwind CSS
[TailwindCSS](https://tailwindcss.com/) is a CSS framework based on utility classes. This means that instead of trying to handroll a lot of our own css classes and define styles for them, we choose from a whole lot of pre-written ones! This can be done by adding classes to your html which match the tailwind classes. For example

### Lets start!
If you're at all like me you get very frustrated and discouraged if something doesn't work right when you're trying to set it up. Therefore I like to start with something that works. So lets get a basic layout to start! We're going to follow their process for installation from the Tailwind [Install Docs](https://tailwindcss.com/docs/installation#installing-tailwind-as-a-post-css-plugin)
```
$ npm install tailwindcss autoprefixer postcss
```
and we can update our `public/index.html` to pull in a css file we will generate using our new deps!
```
// public/index.html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Packrat</title>
    <link rel="stylesheet" href="styles.css">
  </head>
...
```

Then we should init a config file using this command
```
$ npx tailwindcss init
```
This creates a new config file called `./tailwind.config.js` for configuring the tailwindcss cli tool. The default config should look like this:
```
module.exports = {
  purge: []
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
  },
  variants: {
    extend: {},
  },
  plugins: [],
}
```

Since we will want to pre-process the css and only keep the styles we use to minimize the amount of css we 
send over the wire, we can update the `purge` key in our config to tell tailwind exactly where to look for our style classes. 
This can be a regex or path.
```
purge: [
  './src/**/*/cljs',
  './public/index.html'
]
```
Under the hood, tailwind is using a library called purgecss to look through the files matching our purge config key for any matching tailwind css class
names using this regex ```t/[^<>"'`\s]*[^<>"'`\s:]/g ```. Now whenever we set the `production` value for `NODE_ENV` and run our tailwindcss build command,
our css tailwind will remove any styles from our output styles.css file that we don't need. This gives us a ton of flexibility in where our class names live and since
we are building our components in our code we can rest easy as long as we match the regex.

Before we go any further we want to make sure this works! Lets update our hello to use some of these new tailwind
classes. Lets run
```
$ npx tailwindcss build -o public/styles.css

   tailwindcss 2.0.1

   🚀 Building from default CSS... (No input file provided)

   ✅ Finished in 2.85 s
   📦 Size: 3.74MB
   💾 Saved to public/styles.css
```
Two things you'll notice immediately; first that the whole of tailwind was installed at a whopping 3.8MBs and second that
our former `bold-greeting` header is now the same size as our p tag. This is ok. We will use our tailwindcss purge tool to make sure we
have a much smaller output. As for the text, Tailwind overrides all the default
values for text so that we can set our own as we need. This is configurable in some file somewhere and if you're
interested you should check out the themes configuration section of the tailwind docs.

For now I'm going to update the our `bold-greeting` function to make our text 3XL
```clojure
(defn bold-greeting [message]
  [:h1
   [:span.text-3xl message]])
```
And right away (assuming you're using the `npx shadowcljs watch app`) you should see our header get significantly
bigger. I kind of miss the color now, so lets add a second class to make sure we can get our header color back!
```clojure
(defn bold-greeting [message]
  [:h1
   [:span.text-3xl.text-pink-500 message]])
```
In our hiccup based syntax for our reagent app we can continue to easily add classes with our dot syntax which lets us
keep an easy to read class name in our html. You can do the same thing by swapping a `.` with `#` for an html id.

Now we should make sure our production build strips out all the unnecessay classes and gives us a nice clean and small
css.
```
$ NODE_ENV=production tailwind build -o public/style.css


   tailwindcss 2.0.1

   🚀 Building from default CSS... (No input file provided)

   ✅ Finished in 2.87 s
   📦 Size: 10.67KB
   💾 Saved to public/style.css

```
Only 10.67KB! A huge improvement. 

Unfortunately we are now unable to keep adding different styles to our app because we've
deleted everything except the color pink and a 3xl text size. So we will have to rebuild without setting the NODE_ENV to
production. For now I'm going to add this to our package.json as a npm script so I can run
```
$ npm run production-css
```
To do this we take our command from above and update our json to include a production-css script and while we are at it
a css to get the base styles we need:
```json
...
  "scripts": {
    "css": "npx tailwind build -o public/style.css",
    "production-css": "NODE_ENV=production npx tailwind build -o public/style.css"
  },
```

In the future we can do more exploring with tailwind and add themes or update our config to take advantage of all
tailwind has to offer.
