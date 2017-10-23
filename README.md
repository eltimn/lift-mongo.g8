Lift MongoDB Project
--------------------

[g8](https://github.com/foundweekends/giter8) template to get a Lift-MongoDB webapp up and running quickly. The template generates a project that uses [Twitter's Bootstrap](http://twitter.github.com/bootstrap/) and implements the [Mongoauth Lift Module](https://github.com/eltimn/lift-mongoauth). It uses SBT as the build tool.

Usage
=====

Install [SBT](http://www.scala-sbt.org/)

In a shell, run the following in the parent directory of the new project:

    sbt new eltimn/lift-mongo
    cd <project-name>

Please see the README file in your new project for further instructions on configuring and running your app.

Versions
========

Instead of allowing the user to select Scala, Lift, and SBT versions, this template uses predetermined versions. This will hopefully make it easier for new users to get the proper versions of dependent libraries.

License
=======
Written in 2017 by Tim Nelson.

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>

Notes
=====

Use template locally:

    sbt new file:///home/nelly/code/lift-mongo.g8
