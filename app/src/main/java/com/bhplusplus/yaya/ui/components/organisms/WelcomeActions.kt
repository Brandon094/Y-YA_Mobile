package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bhplusplus.yaya.R
import com.bhplusplus.yaya.ui.components.atoms.PoweredByBH
import com.bhplusplus.yaya.ui.components.atoms.YayaPrimaryButton
import com.bhplusplus.yaya.ui.components.atoms.YayaSecondaryButton

@Composable
fun WelcomeActions(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YayaPrimaryButton(
            text = stringResource(R.string.welcome_login_button),
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        YayaSecondaryButton(
            text = stringResource(R.string.welcome_register_button),
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        PoweredByBH()
    }
}
