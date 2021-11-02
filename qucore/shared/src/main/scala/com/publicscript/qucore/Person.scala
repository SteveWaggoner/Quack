package com.publicscript.qucore

case class Person(
                   id: Int,

                   //for ancesters and possibly users
                   wikitreeId: String,

                   //for users only
                   gedMatchId: String,
                   gedMatchId2: String,

                   //info for ancesters (or maybe users)
                   firstAndMiddleName: String,
                   lastName: String,
                   suffix: String,

                   birthDate: String,
                   birthPlace: String,
                   deathDate: String,
                   deathPlace: String,

                   fatherId: Int,
                   motherId: Int,
                 )

