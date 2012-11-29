Twitter Bootstrap
-----------------

Bootstrap v2.1.1 is included.

MongoDB
-------

This app uses MongoDB. Therefore, you will need to either have it installed locally, or use one of
the cloud providers and configure it in your props file. See config.MongoConfig for more info.

Building
--------

This app requires sbt 0.12. To build for the first time, run:

    bash\$ sbt
    > ~;container:start; container:reload /

That will start the app and automatically reload it whenever sources are modified. It will be running
on http://localhost:8080

Less
----

The [less-sbt](https://github.com/softprops/less-sbt) plugin is used to compile the bootstrap less files as well as your own.

* `src/main/less/styles.less` acts a manifest file that lists all of the other less files that are to be included in the build.
* `src/main/less/variables.less` is for setting colors, fonts, etc.
* `src/main/less/custom.less` is where you can add custom styles for your app.
* `src/main/less/bootstrap-2.1.1` are the original files and should not be changed.

You can also add other less files to the build by adding them to styles.less.

**Note:** In order to trigger sbt to re-compile the less files, you need to save styles.less.

JavaScript
----------

The [sbt-closure](https://github.com/eltimn/sbt-closure) plugin is used to compile JavaScript files.

* `src/main/javascript/script.jsm` is the JavaScript manifest file that lists all of the JavaScript files that are to be included in the build.

**Note:** In order to trigger sbt to re-compile the JavaScript files, you need to save script.jsm.

User Model
----------

This app implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth).
The registration and login implementation is based on
[research done by Google](http://sites.google.com/site/oauthgoog/UXFedLogin) a few years ago
and is similar to Amazon.com and Buy.com. It's different than what most people seem to expect,
but it can easily be changed to suit your needs since most of the code is part of your project.
