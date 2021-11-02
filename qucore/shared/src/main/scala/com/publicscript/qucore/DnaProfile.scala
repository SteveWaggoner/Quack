package com.publicscript.qucore

case class DnaProfile(
                       //profile name for display
                       name: String,

                       //descendant information
                       personId: Int,

                       //the descendents profile
                       dnaSegments: List[DnaProfile.Segment]
                     )

object DnaProfile {

  case class Group(
                    //for display
                    name: String,           // ex. Abraham Waggoner + Sarah Huggins
                    color: String,          // for display

                    //actual ancester info (most have at least one of these and best is all three)
                    ancestorId: Int,        // Person.Id
                    ancestorFatherId: Int,  // Person.Id
                    ancestorMotherId: Int,  // Person.Id
                  )

  case class Match(
                    //most have person id since how we get gedmatch
                    personId: Int,

                    //optionally display label
                    name: String,
                  )

  case class Segment(
                      //ancestoer info
                      group: Group,
                      groupConfidence: Int,

                      //match info
                      segMatch: Match,

                      //DNA segment info
                      chr: String,
                      start: Long,
                      end: Long,
                      cM: Double,
                      SNPs: Int
                    )

  def loadFromFile(json: String): DnaProfile = {
    return null
  }

}

