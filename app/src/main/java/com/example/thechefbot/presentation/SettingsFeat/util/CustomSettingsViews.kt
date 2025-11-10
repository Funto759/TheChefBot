package com.example.thechefbot.presentation.SettingsFeat.util


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.thechefbot.R
import com.example.thechefbot.navigation.Routes


@Composable
fun SettingsItemView(
    modifier: Modifier = Modifier
    ,text : String
    , onClick : () -> Unit
    , leadingIcon : ImageVector
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = leadingIcon,  // requires material-icons-extended
            contentDescription = "Setting items",
            modifier = modifier
                .size(24.dp)
                .padding(end = 8.dp),
            tint = colorResource(R.color.orange)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,  // requires material-icons-extended
            contentDescription = "Profile details",
            modifier = modifier.size(24.dp),
            tint = colorResource(R.color.orange)
        )
    }

    Spacer(modifier = modifier.height(16.dp))
}


@Composable
fun SettingsSwitchItem(
    modifier: Modifier = Modifier,
    pushNotificationsEnabled: Boolean,
    togglePushNotifications: (Boolean) -> Unit,
    label : String,
    leadingIcon: ImageVector

    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = "Setting items",
            modifier = modifier
                .size(24.dp)
                .padding(end = 8.dp),
            tint = colorResource(R.color.orange)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.weight(1f)
        )
        Switch(
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(R.color.orange),
                checkedTrackColor =Color.LightGray,
                checkedBorderColor = colorResource(R.color.pink),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = colorResource(R.color.pink),
            ),
            checked = pushNotificationsEnabled,
            onCheckedChange = togglePushNotifications
        )
    }
}

@Composable
fun SettingsTittleView(modifier: Modifier = Modifier, label : String) {
    HorizontalDivider(color = colorResource(R.color.orange))
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = label,
        color = Color.LightGray,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsUserDetailsView(
    modifier: Modifier = Modifier,
    email : String,
    onClick: () -> Unit,
    painter : Painter
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular profile image
        Image(
            painter = painter, // placeholder image resource
            contentDescription = "Profile picture",
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Name text
        Text(
            text = email,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = modifier.weight(1f)  // take remaining space
        )
        // Trailing arrow icon
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,  // requires material-icons-extended
            contentDescription = "Profile details",
            modifier = modifier.size(24.dp),
            tint = colorResource(R.color.orange)
        )
    }

}