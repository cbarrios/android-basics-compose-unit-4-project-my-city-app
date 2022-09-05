package com.example.flavorsofmiami.data.local

import com.example.flavorsofmiami.R
import com.example.flavorsofmiami.data.model.Category
import com.example.flavorsofmiami.data.model.Recommendation

object Datasource {

    val recommendations = listOf(
        Recommendation(
            R.string.church_cgcc_title,
            R.string.church_cgcc_description,
            Category.CHURCH
        ),
        Recommendation(
            R.string.church_gesu_title,
            R.string.church_gesu_description,
            Category.CHURCH
        ),
        Recommendation(
            R.string.church_little_flower_title,
            R.string.church_little_flower_description,
            Category.CHURCH
        ),
        Recommendation(
            R.string.wedding_biltmore_title,
            R.string.wedding_biltmore_description,
            Category.WEDDING
        ),
        Recommendation(
            R.string.wedding_fairchild_title,
            R.string.wedding_fairchild_description,
            Category.WEDDING
        ),
        Recommendation(
            R.string.wedding_walton_title,
            R.string.wedding_walton_description,
            Category.WEDDING
        ),
        Recommendation(
            R.string.hotel_biltmore_title,
            R.string.hotel_biltmore_description,
            Category.HOTEL
        ),
        Recommendation(
            R.string.hotel_fountain_blue_title,
            R.string.hotel_fountain_blue_description,
            Category.HOTEL
        ),
        Recommendation(
            R.string.hotel_guitar_title,
            R.string.hotel_guitar_description,
            Category.HOTEL
        )
    )
}