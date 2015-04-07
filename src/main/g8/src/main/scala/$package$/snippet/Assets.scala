package $package$
package snippet

import net.liftweb.common._
import net.liftweb.http.LiftRules
import net.liftweb.json._
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.util.Props

object Assets {
  private lazy val assetsMap: Map[String, String] = {
    if (Props.mode == Props.RunModes.Development)
      Map.empty
    else {
      (LiftRules
        .loadResourceAsString("/assets.json")
        .flatMap { s => tryo(JsonParser.parse(s)) }
      ) match {
        case Full(jo: JObject) => jo.values.mapValues(_.toString)
        case _ => Map.empty
      }
    }
  }

  private def assetPath(asset: String): String =
    assetsMap.getOrElse(asset, asset)

  def css = {
    "* [href]" #> assetPath(S.attr("src").getOrElse("/styles.min.css"))
  }

  def js = {
    "* [src]" #> assetPath(S.attr("src").getOrElse("/scripts.min.js"))
  }
}
