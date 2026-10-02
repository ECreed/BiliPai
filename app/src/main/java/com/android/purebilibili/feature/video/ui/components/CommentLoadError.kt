package com.android.purebilibili.feature.video.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.purebilibili.core.ui.components.AppText
import com.android.purebilibili.core.ui.components.AppTextButton

@Composable
internal fun CommentLoadError(message: String, onRetry: () -> Unit) {
    val appearance = rememberVideoCommentAppearance()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppText(message, color = appearance.secondaryTextColor, textAlign = TextAlign.Center)
        AppTextButton(onClick = onRetry, modifier = Modifier.heightIn(min = 48.dp)) {
            AppText("重试")
        }
    }
}
