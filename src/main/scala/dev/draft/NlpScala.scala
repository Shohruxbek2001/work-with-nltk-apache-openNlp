package dev.draft

import net.ruippeixotog.scalascraper.browser._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import opennlp.tools.postag.{POSModel, POSTaggerME}
import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}
import opennlp.tools.util.Span

import scala.io.Source

object NlpScala {
  def main(args: Array[String]): Unit = {
    val browser = JsoupBrowser()
    val doc = browser.get("https://gist.githubusercontent.com/nzhukov/b66c831ea88b4e5c4a044c952fb3e1ae/raw/7935e52297e2e85933e41d1fd16ed529f1e689f5/A%2520Brief%2520History%2520of%2520the%2520Web.txt")
    val articles = doc >> elementList("body")
    val text = for (article <- articles) yield article.extract(element("body")).text
    val textForNlp = text.mkString
    val tokenizerModelIn = getClass.getResourceAsStream("/resources/models/tokenizer/token.model")
    val posModelIn = getClass.getResourceAsStream("resources/models/pos/pos.model")

    val tokenizerModel = new TokenizerModel(tokenizerModelIn)
    val posModel = new POSModel(posModelIn)

    val tokenizer = new TokenizerME(tokenizerModel)
    val posTagger = new POSTaggerME(posModel)

    val tokens = tokenizer.tokenize(textForNlp)

    val posTags = posTagger.tag(tokens)

    val posCounts = posTags.groupBy(identity).mapValues(_.length)

    posCounts.foreach { case (pos, count) =>
      println(s"$pos: $count")
    }

  }
}