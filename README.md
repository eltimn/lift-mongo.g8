Lift MongoDB Project
--------------------

[g8](http://github.com/n8han/giter8) template to get a Lift-MongoDB webapp up and running quickly. The template generates a project that uses [Twitter's Bootstrap](http://twitter.github.com/bootstrap/) and implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth). It uses SBT as the build tool.

Usage
=====

Install [giter8](http://github.com/n8han/giter8#readme) for more information.

Install [SBT](http://www.scala-sbt.org/)

In a shell, run the following in the parent directory of the new project:

    g8 eltimn/lift-mongo
    cd <project-name>

Please see the README file in your new project for further instructions on configuring and running your app.

Versions
========

Instead of allowing the user to select Scala, Lift, and SBT versions, this template uses predetermined versions. This will hopefully make it easier for new users to get the proper versions of dependent libraries.

Notes
=====

Use template locally:

    g8 file://~/code/lift-mongo.g8
