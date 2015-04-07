Twitter Bootstrap
-----------------

Bootstrap v3 is included.

MongoDB
-------

This app uses MongoDB. Therefore, you will need to either have it installed locally, or use one of the cloud providers and configure it in your props file. See config.MongoConfig for more info.

A `docker-compose.yml` file is included if you're using [docker-compose](https://docs.docker.com/compose/).

Building
--------

This app requires [sbt](http://www.scala-sbt.org/). To build for the first time, run:

    bash\$ sbt
    > ~;container:start; container:reload /

The alias `ccr` is provided so you don't have to remember that.

That will start the app and automatically reload it whenever sources are modified. It will be running on http://localhost:8080

sbt-web
-------

[sbt-web](https://github.com/sbt/sbt-web) and associated plugins are used to handle JavaScript, LESS, and font files.

WebJars
-------

WebJars are used to retrieve the jQuery and Twitter bootstrap dependencies.

User Model
----------

This app implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth). The registration and login implementation is based on [research done by Google](http://sites.google.com/site/oauthgoog/UXFedLogin) a few years ago and is similar to Amazon.com and Buy.com. It's different than what most people seem to expect, but it can easily be changed to suit your needs since most of the code is part of your project.

BrowserSync
-----------

*Requires: [NodeJS](https://nodejs.org/)*

The sbt build includes a `browserSync` task that will update a file every time the xsbt-web-plugin reloads. This is used in conjuction with the simple node script in the _bs_ folder.

To use, first start the xsbt-web-plugin using the following command:

    sbt> ~ ;container:start ;container:reload / ;browserSync

Or, use the provided alias `ccrs`.

Then, in a separate terminal, go to the bs directory and run:

  bash\$ npm install
  bash\$ node bs.js

Once that's running it will display the port that you need to point your browser to, default is 3000.

Now, any time any changes are made and the container reloads, the browser will automatically reload as well. It will also reload when any .html files are changed.

Note: this was put in the bs directory because having a package.json file in the top directory caused problems with sbt-web.

Packaging
---------

The `package` task will package the app into a WAR file. The `warPostProcess` function handles creating an SHA-1 digest of each asset file and renames them with the digest. The `Assets` snippet handles figuring out what the digest is for each file.
