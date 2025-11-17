package com.digitalbackpack.subscription.ui.screens.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.digitalbackpack.subscription.SubscriptionApp
import com.digitalbackpack.subscription.data.model.SubscriptionCategory
import java.text.SimpleDateFormat
import java.util.*

/**
 * 添加/编辑订阅界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    subscriptionId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = viewModel(
        factory = AddEditViewModelFactory(
            LocalContext.current.applicationContext as SubscriptionApp,
            (LocalContext.current.applicationContext as SubscriptionApp).repository,
            subscriptionId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    
    LaunchedEffect(Unit) {
        if (subscriptionId != -1L) {
            viewModel.loadSubscription(subscriptionId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (subscriptionId == -1L) "添加订阅" else "编辑订阅") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 项目名称
            OutlinedTextField(
                value = uiState.projectName,
                onValueChange = { viewModel.updateProjectName(it) },
                label = { Text("项目名称 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 分类
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("分类") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    SubscriptionCategory.getAllCategories().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.updateCategory(category)
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
            
            // 网站URL
            OutlinedTextField(
                value = uiState.websiteUrl,
                onValueChange = { viewModel.updateWebsiteUrl(it) },
                label = { Text("网站URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            
            // 账号
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text("账号 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // 密码
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("密码 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "隐藏" else "显示")
                    }
                }
            )
            
            // 到期日期
            OutlinedTextField(
                value = dateFormatter.format(Date(uiState.expiryDate)),
                onValueChange = {},
                readOnly = true,
                label = { Text("到期日期 *") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 价格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = if (uiState.price == 0.0) "" else uiState.price.toString(),
                    onValueChange = { 
                        viewModel.updatePrice(it.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("价格") },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = uiState.currency,
                    onValueChange = { viewModel.updateCurrency(it) },
                    label = { Text("货币") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            // 续费周期
            OutlinedTextField(
                value = uiState.renewalPeriodDays.toString(),
                onValueChange = { 
                    viewModel.updateRenewalPeriod(it.toIntOrNull() ?: 30)
                },
                label = { Text("续费周期（天）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // 提醒天数
            OutlinedTextField(
                value = uiState.reminderDaysBefore.toString(),
                onValueChange = { 
                    viewModel.updateReminderDays(it.toIntOrNull() ?: 1)
                },
                label = { Text("提前提醒（天）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // 启用提醒
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "启用到期提醒",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                )
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = { viewModel.updateReminderEnabled(it) }
                )
            }
            
            // 备注
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("备注") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // 保存按钮
            Button(
                onClick = {
                    viewModel.saveSubscription()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.projectName.isNotBlank() && 
                         uiState.username.isNotBlank() && 
                         uiState.password.isNotBlank()
            ) {
                Text("保存")
            }
        }
    }
    
    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.expiryDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.updateExpiryDate(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

