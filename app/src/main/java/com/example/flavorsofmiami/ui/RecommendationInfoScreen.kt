package com.example.flavorsofmiami.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flavorsofmiami.data.local.Datasource
import com.example.flavorsofmiami.data.model.Recommendation
import com.example.flavorsofmiami.ui.theme.FlavorsOfMiamiTheme

// Reusable component for the recommendation details.
// Image, title and description in a column. Back handler must be supported.
@Composable
fun RecommendationInfoScreen(
    modifier: Modifier = Modifier,
    recommendation: Recommendation? = Datasource.recommendations.first(),
    onNavigateUp: () -> Unit = {}
) {
    BackHandler {
        onNavigateUp()
    }
    recommendation?.let {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                painter = painterResource(id = it.image),
                contentDescription = stringResource(id = it.title),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = stringResource(id = it.title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = it.description),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RecommendationInfoScreenPreview() {
    FlavorsOfMiamiTheme {
        Surface {
            RecommendationInfoScreen()
        }
    }
}