package $package$
package model

class UserSpec extends BaseMongoSessionWordSpec {
  "User" should {
    "create, validate, save, and retrieve properly" in {

      val userPass = "testpass1"
      // create a new User instance
      val newUser = User.createRecord
        .email("test@liftweb.net")

      newUser.password(userPass)

      val errs = newUser.validate
      if (errs.length > 1) {
        fail("Validation error: "+errs.mkString(", "))
      }

      newUser.name("Test")
      newUser.username("Test")
      newUser.validate.length should equal (0)

      // save to db
      newUser.password.hashIt
      newUser.save

      // retrieve from db and compare
      val userFromDb = User.find(newUser.id.get)
      userFromDb.isDefined should equal (true)
      userFromDb.map(u => u.id.get should equal (newUser.id.get))
    }

    "Support password properly" in {

      val userPass = "testpass2"
      // create a new User instance
      val newUser = User.createRecord
        .email("test2@liftweb.net")
        .name("Test2")
        .username("Test2")
        .password(userPass, true)

      // check password
      newUser.password.isMatch("xxxxx") should equal (false)
      newUser.password.isMatch(userPass) should equal (true)

      newUser.validate.length should equal (0)

      // save to db
      newUser.save

      // retrieve from db and compare
      val userFromDb = User.find(newUser.id.get)

      userFromDb.isDefined should equal (true)
      userFromDb.map(u => {
        u.id.get should equal (newUser.id.get)
        u.password.isMatch("xxxxx") should equal (false)
        u.password.isMatch(userPass) should equal (true)
      })
    }
  }
}
