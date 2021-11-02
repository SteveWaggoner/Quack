package com.publicscript.qucore

import junit.framework.TestCase
import org.junit.Assert._


class GedMatchTests extends TestCase {

  override def setUp {
  }

  def testJsonGedmatch {


    val profile = new DnaProfile("My Profile", 1, Nil)
    val profileJson = upickle.default.write[DnaProfile](profile)

    println(profileJson)



    import com.github.tototoshi.csv._

    val x = CSVParser.parse("x,y,z\n1,2,3", '\\', ',', '\"')
    println(x)

    val csv = CSVReader.open("jvm/src/test/data/DNA Painter Export - Steve Waggoner (identified only) (1).csv")

    println(csv.allWithHeaders())


  }

}