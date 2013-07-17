Lift MongoDB Project
--------------------

[g8](http://github.com/n8han/giter8) template to get a Lift-MongoDB webapp up and running quickly.
The template generates a project that uses [Twitter's Bootstrap](http://twitter.github.com/bootstrap/)
and implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth). It uses SBT 0.12
as the build tool.

Usage
=====

Install giter8 (g8) - [Readme](http://github.com/n8han/giter8#readme) for more information.

Install SBT 0.12 - [Setup](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html) for more information.

In a shell, run the following in the parent directory of the new project:

    g8 eltimn/lift-mongo
    cd <project-name>

Please see the README file in your new project for further instructions on configuring and running your app.

Versions
========

Instead of allowing the user to select Scala, Lift, and SBT versions, this template uses predetermined versions.
This will hopefully make it easier for new users to get the proper versions of dependent libraries.
