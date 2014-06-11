Mangue - Online Manga Reader
=====================

The Mangue is a reader for online mangas avaliable at [Manga Panda](http://www.mangapanda.com).

![Logo](https://raw.githubusercontent.com/repinel/Mangue/master/images/mangue_logo.jpg)

Feedbacks are welcome. The project was mainly tested on Kindle 4.


Build Instructions
-------

1. Install the requirements.
  * Java 1.4 or greater.
  * Apache Ant.
2. Create the `.kindle` directory in your user home.
  * Copy the following from your Kindle `/lib`
    * `Kindlet-*.jar`
    * `json_simple-*.jar`
    * `log4j-*.jar`
  * Copy the `developer.keystore` file from your Kindle.
3. Create the `local.properties` file the project root.
  * Add the line `keystore.alias.prefix=test`. Replace `test` with your keystore alias prefix.
  * Add the line `keystore.storepass=password` to the file. Replace `password` with the keystore password.
  * Both values, `test` and `password` are the default for Amazon's keystore. At least for the Kindle 4.
4. Run the command `ant`.
5. The signed package `Mangue.azw2` should have been created in the `build` directory.

Installation
-------

To install Mangue, copy the signed package `Mangue.azw2` into the your Kindle `documents` directory.

Usage
-------

Mangue is very simple to be used. Once you start it, you should see the message bellow:

```
Welcome to Mangue - Online Manga Reader.
Copyright ©2014 Roque Pinel

Use the menu option to search and add mangas to your list.

All mangas available here are loaded from the www.mangapanda.com website.
Mangue is not affiliated with Manga Panda.
```

As stated, from the menu it is possible to search and add mangas to your favorites. The list of favorities mangas is normally the first screen displayed.

You can select a manga using the 5-way controller. Once a manga is selected, its chapters are loaded and displayed.

Chapters can also be selected using the 5-way controller. Once a chapter is selected, its first page is load.

To navigate through pages, use the previous page and next page buttons.

In order to rotate the page, if needed, press the selected key (from the 5-way controller) once.

To return to the previous screen, always use the back button.

If you exit while reading a page, Mangue will return to that same page once it is restarted.

Enjoy!!

License
-------

Mangue is under the Apache 2.0 License (see the [LICENSE](https://raw.github.com/repinel/Mangue/master/doc/Apache-2.0) file).

Contributing
-------

Contribution and feedback are welcome. If you find an issue, feel free to report it. Pull requests are encouraged.

---

Mangue is not affiliated with Manga Panda.

