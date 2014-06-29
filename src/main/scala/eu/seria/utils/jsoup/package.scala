package eu.seria.utils

import org.jsoup.nodes.Element

package object jsoup {

  implicit class PimpedJsoupElement(element: Element) {

    def href: String = element.attr("href")

    def src: String = element.attr("src")

    def content: String = element.attr("content")

  }

}
