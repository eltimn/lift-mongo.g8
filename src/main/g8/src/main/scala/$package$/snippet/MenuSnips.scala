package $package$
package snippet

import lib.AppHelpers

import scala.xml.NodeSeq

import net.liftweb._
import common._
import http.{LiftRules, S}
import sitemap._
import util.Helpers._
import scala.xml.Text

object GroupMenu extends AppHelpers {
  def render(in: NodeSeq): NodeSeq = {
    for {
      group <- S.attr("group") ?~ "Group not specified"
      sitemap <- LiftRules.siteMap ?~ "Sitemap is empty"
      request <- S.request ?~ "Request is empty"
      curLoc <- request.location ?~ "Current location is empty"
    } yield ({
      val currentClass = S.attr("current_class").openOr("active")
      sitemap.locForGroup(group) flatMap { loc =>
        if (loc.menu.kids.length == 0) {
          val styles = if (curLoc.name == loc.name) currentClass else ""
          <li class={styles}>{SiteMap.buildLink(loc.name)}</li>
        }
        else {
          val styles =
            if (loc.menu.kids.exists { _.loc.name == curLoc.name }) currentClass
            else ""

          <li class={styles + " dropdown"}>
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">{loc.linkText.openOr(Text("Empty Name"))} <b class="caret"></b></a>
            <ul class="dropdown-menu">{ for (kid <- loc.menu.kids) yield <li>{SiteMap.buildLink(kid.loc.name)}</li> }</ul>
          </li>
        }
      }
    }): NodeSeq
  }
}
