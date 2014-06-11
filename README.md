Mangue - Online Manga Reader
=====================

The Mangue is a reader for online mangas avaliable at [Manga Panda](http://www.mangapanda.com).

![Logo](https://raw.githubusercontent.com/repinel/Mangue/master/images/mangue_logo.jpg)

Feedbacks is welcome. The project was mainly tested on Kindle 4.

Mangue is not affiliated with Manga Panda.

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

License
-------

Mangue is under the Apache 2.0 License (see the [LICENSE](https://raw.github.com/repinel/Mangue/master/doc/Apache-2.0) file).
