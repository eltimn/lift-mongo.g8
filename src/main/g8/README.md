# Twitter Bootstrap

Bootstrap v2.0.1 is included.

# MongoDB

This app uses MongoDB. Therefore, you will need to either have it installed locally, or use one of
the cloud providers and configure it in your props file. See config.MongoConfig for more info.

# Building

This app uses sbt 0.12. To build for the first time, run:

    bash\$ sbt
    > ~;container:start; container:reload /

That will start the app and automatically reload it whenever sources are modified. It will be running
on http://localhost:8080

# User Model

This app implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth).
The registration and login implementation is based on
[research done by Google](http://sites.google.com/site/oauthgoog/UXFedLogin) a few years ago
and is similar to Amazon.com and Buy.com. It's different than what most people seem to expect,
but it can easily be changed to suit your needs since most of the code is part of your project.
