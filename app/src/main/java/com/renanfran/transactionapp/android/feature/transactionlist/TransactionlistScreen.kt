package com.renanfran.transactionapp.android.feature.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.renanfran.transactionapp.android.R
import com.renanfran.transactionapp.android.feature.add_transaction.ExpenseDropDown
import com.renanfran.transactionapp.android.feature.home.TransactionItem
import com.renanfran.transactionapp.android.utils.Utils
import com.renanfran.transactionapp.android.feature.home.HomeViewModel
import com.renanfran.transactionapp.android.widget.ExpenseTextView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state = viewModel.expenses.collectAsState(initial = emptyList())
    var filterType by remember { mutableStateOf("Todos") }
    var dateRange by remember { mutableStateOf("Sempre") }
    var menuExpanded by remember { mutableStateOf(false) }

    val filteredTransactions = when (filterType) {
        "Despesa" -> state.value.filter { it.type == "Despesa" }
        "Receita" -> state.value.filter { it.type == "Receita" }
        else -> state.value
    }

    val filteredByDateRange = filteredTransactions.filter { transaction ->
        true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )

                ExpenseTextView(
                    text = "Transações",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { menuExpanded = !menuExpanded },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit = slideOutVertically(targetOffsetY = { -it  }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            ExpenseDropDown(
                                listOfItems = listOf("Todos", "Despesa", "Receita"),
                                onItemSelected = { selected ->
                                    filterType = selected
                                    menuExpanded = false
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExpenseDropDown(
                                listOfItems = listOf("Ontem", "Hoje", "30 dias", "90 dias", "Ano Passado"),
                                onItemSelected = { selected ->
                                    dateRange = selected
                                    menuExpanded = false 
                                }
                            )
                        }
                    }
                }
                items(filteredByDateRange) { item ->
                    val icon = Utils.getItemIcon(item)
                    val amount = if (item.type == "Receita") item.amount else item.amount * -1

                    TransactionItem(
                        title = item.title,
                        amount = Utils.formatCurrency(amount),
                        icon = icon!!,
                        date = item.date,
                        color = if (item.type == "Receita") Color.Green else Color.Red,
                        Modifier.animateItemPlacement(tween(100))
                    )
                }
            }
        }
    }
}
