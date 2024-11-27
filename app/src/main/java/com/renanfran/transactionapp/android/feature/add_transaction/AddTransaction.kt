@file:OptIn(ExperimentalMaterial3Api::class)

package com.renanfran.transactionapp.android.feature.add_transaction

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.renanfran.transactionapp.android.R
import com.renanfran.transactionapp.android.base.AddTransactionNavigationEvent
import com.renanfran.transactionapp.android.base.NavigationEvent
import com.renanfran.transactionapp.android.utils.Utils
import com.renanfran.transactionapp.android.data.model.TransactionEntity
import com.renanfran.transactionapp.android.ui.theme.InterFontFamily
import com.renanfran.transactionapp.android.ui.theme.LightGrey
import com.renanfran.transactionapp.android.ui.theme.Typography
import com.renanfran.transactionapp.android.widget.ExpenseTextView

@Composable
fun AddTransaction(
    navController: NavController,
    isIncome: Boolean,
    transactionId: Int? = null, // New parameter for editing
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val menuExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId) // Load transaction if editing
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                AddTransactionNavigationEvent.MenuOpenedClicked -> {
                    menuExpanded.value = true
                }
                else -> {}
            }
        }
    }

    val transactionData = viewModel.transactionData.collectAsState().value

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, card) = createRefs()

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                // Back button
                Icon(
                    painter = painterResource(id = R.drawable.ic_back), // Replace with your back icon
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            viewModel.onEvent(AddTransactionUiEvent.OnBackPressed)
                        }
                )
                // Title text
                ExpenseTextView(
                    text = "${if (isIncome) "Receita" else "Despesa"}",
                    style = Typography.titleLarge,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            DataForm(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onAddTransactionClick = {
                    viewModel.onEvent(AddTransactionUiEvent.OnAddTransactionClicked(it))
                },
                isIncome = isIncome,
                transactionData = transactionData // Prefill form with existing data if editing
            )
        }
    }
}

@Composable
fun DataForm(
    modifier: Modifier,
    onAddTransactionClick: (model: TransactionEntity) -> Unit,
    isIncome: Boolean,
    transactionData: TransactionEntity? = null // Optional parameter for existing data
) {
    // Initialize state variables with default values
    val name = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val date = remember { mutableLongStateOf(0L) }
    val type = remember { mutableStateOf(if (isIncome) "Receita" else "Despesa") }
    val dateDialogVisibility = remember { mutableStateOf(false) }

    // Update state variables when transactionData changes
    LaunchedEffect(transactionData) {
        if (transactionData != null) {
            name.value = transactionData.title
            amount.value = transactionData.amount.toString()
            date.value = transactionData.date?.let { Utils.parseDate(it) as Long } ?: 0L
            type.value = transactionData.type
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleComponent(title = "Descrição")
        OutlinedTextField(
            value = name.value,
            onValueChange = { newValue ->
                name.value = newValue
            },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { ExpenseTextView(text = "Descrição") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.size(24.dp))

        TitleComponent("Valor")
        OutlinedTextField(
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue.filter { it.isDigit() || it == '.' }
            },
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = { text ->
                val out = "R$" + text.text
                val currencyOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return offset + 2 // Adjust for the "R$" prefix
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        return if (offset > 1) offset - 2 else 0 // Adjust for the "R$" prefix
                    }
                }

                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { ExpenseTextView(text = "Valor") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.size(24.dp))

        TitleComponent("Data")
        OutlinedTextField(
            value = if (date.longValue == 0L) "" else Utils.formatDateToHumanReadableForm(date.longValue),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogVisibility.value = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
            ),
            placeholder = { ExpenseTextView(text = "Selecione a Data") }
        )

        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = {
                val model = TransactionEntity(
                    id = transactionData?.id, // Use the existing ID if editing
                    title = name.value,
                    amount = amount.value.toDoubleOrNull() ?: 0.0,
                    date = Utils.formatDateToHumanReadableForm(date.longValue),
                    type = type.value
                )
                onAddTransactionClick(model)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            ExpenseTextView(
                text = if (transactionData != null) "Editar" else "Adicionar", // Show "Edit" or "Add" based on the context
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }

    if (dateDialogVisibility.value) {
        ExpenseDatePickerDialog(onDateSelected = {
            date.longValue = it
            dateDialogVisibility.value = false
        }, onDismiss = {
            dateDialogVisibility.value = false
        })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickerDialog(
    onDateSelected: (date: Long) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            ExpenseTextView(text = "Confirmar")
        }
    }, dismissButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            ExpenseTextView(text = "Cancelar")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TitleComponent(title: String) {
    ExpenseTextView(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = LightGrey
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun ExpenseDropDown(listOfItems: List<String>, onItemSelected: (item: String) -> Unit) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val selectedItem = remember {
        mutableStateOf(listOfItems[0])
    }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        OutlinedTextField(
            value = selectedItem.value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(fontFamily = InterFontFamily, color = Color.Black),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

            )
        )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { }) {
            listOfItems.forEach {
                DropdownMenuItem(text = { ExpenseTextView(text = it) }, onClick = {
                    selectedItem.value = it
                    onItemSelected(selectedItem.value)
                    expanded.value = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddTransaction() {
    AddTransaction(rememberNavController(), true)
}

