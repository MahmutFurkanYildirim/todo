package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.ui.components.TodoItem
import com.furkanyildirim.learningcompose.ui.common.displayName
import com.furkanyildirim.learningcompose.ui.navigation.BottomNavItem
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import com.furkanyildirim.learningcompose.viewmodel.TodoViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectTapGestures
import java.util.Calendar
import java.util.Locale

private enum class SortOption {
    Newest,
    DueSoon,
    Alphabetical
}

private enum class LayoutMode {
    List,
    Board
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel,
    onTodoClick: (Todo) -> Unit = {},
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenFocusMode: () -> Unit,
    onOpenAddTodo: () -> Unit,
    recentSearches: List<String>
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.All) }
    val filterSheetState = rememberModalBottomSheetState()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var onlyOverdue by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.Newest) }
    var layoutMode by remember { mutableStateOf(LayoutMode.List) }
    var showMoreActions by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val navItems = listOf(BottomNavItem.Today, BottomNavItem.All, BottomNavItem.Completed, BottomNavItem.Focus)
    val suggestedProjects by remember(todos) {
        derivedStateOf {
            todos.map { it.project.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .take(8)
        }
    }
    val suggestedTags by remember(todos) {
        derivedStateOf {
            todos.flatMap { parseTags(it.tags) }
                .filter { it.isNotBlank() }
                .distinct()
                .take(12)
        }
    }

    val filteredTodos by remember(
        todos,
        selectedTab,
        searchQuery,
        selectedCategory,
        onlyOverdue,
        sortOption
    ) {
        derivedStateOf {
            val now = System.currentTimeMillis()
            todos
                .filter { todo ->
                    when (selectedTab) {
                        BottomNavItem.Today -> isTodayPriority(todo, now)
                        BottomNavItem.All -> !todo.isCompleted
                        BottomNavItem.Completed -> todo.isCompleted
                        BottomNavItem.Focus -> true
                    }
                }
                .filter { todo ->
                    searchQuery.isBlank() || matchesSearch(
                        todo = todo,
                        query = searchQuery,
                        context = context
                    )
                }
                .filter { todo ->
                    selectedCategory == null || todo.category == selectedCategory?.name
                }
                .filter { todo ->
                    !onlyOverdue || (todo.dueDate != null && todo.dueDate < now && !todo.isCompleted)
                }
                .let { list ->
                    when (selectedTab) {
                        BottomNavItem.Today -> {
                            list.sortedWith(
                                compareByDescending<Todo> { todayPriorityScore(it, now) }
                                    .thenBy { it.dueDate ?: Long.MAX_VALUE }
                            )
                        }
                        BottomNavItem.Focus -> list
                        else -> {
                            when (sortOption) {
                                SortOption.Newest -> list.sortedByDescending { it.id }
                                SortOption.DueSoon -> list.sortedWith(
                                    compareBy<Todo> { it.dueDate == null }.thenBy { it.dueDate ?: Long.MAX_VALUE }
                                )
                                SortOption.Alphabetical -> list.sortedBy { it.title.lowercase() }
                            }
                        }
                    }
                }
        }
    }
    val boardSections by remember(filteredTodos, context) {
        derivedStateOf {
            filteredTodos.groupBy { todo ->
                todo.project.ifBlank { context.getString(R.string.label_project_none) }
            }.toSortedMap()
        }
    }

    LaunchedEffect(searchQuery) {
        delay(400)
        if (searchQuery.isNotBlank()) {
            viewModel.rememberSearch(searchQuery)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_neon_todos),
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary
                    ),
                    actions = {
                        IconButton(onClick = onOpenAddTodo) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.content_add_todo)
                            )
                        }
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.content_open_view_options)
                            )
                        }
                        Box {
                            IconButton(onClick = { showMoreActions = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.content_open_more_actions)
                                )
                            }
                            DropdownMenu(
                                expanded = showMoreActions,
                                onDismissRequest = { showMoreActions = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_open_dashboard)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Insights,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreActions = false
                                        onOpenStats()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.title_settings)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreActions = false
                                        onOpenSettings()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_open_appearance)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreActions = false
                                        onThemeChange(!isDarkTheme)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            NeonBarContainer(position = NeonBarPosition.Bottom) {
                NavigationBar(containerColor = Color.Transparent) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = item != BottomNavItem.Focus && selectedTab == item,
                            onClick = {
                                if (item == BottomNavItem.Focus) {
                                    onOpenFocusMode()
                                } else {
                                    selectedTab = item
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes)
                                )
                            },
                            label = { Text(stringResource(item.titleRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        CyberpunkBackground(
            modifier = Modifier
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.search_todos_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.content_search)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.content_clear)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isSearchFocused = it.isFocused }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                if (isSearchFocused && recentSearches.isNotEmpty()) {
                    RecentSearchesHeading(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(recentSearches, key = { it }) { recent ->
                            FilterChip(
                                selected = false,
                                onClick = { searchQuery = recent },
                                label = { Text(recent) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }

                if (selectedTab == BottomNavItem.Today) {
                    Text(
                        text = stringResource(R.string.label_today_focus),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (selectedCategory != null || onlyOverdue || sortOption != SortOption.Newest) {
                    Text(
                        text = stringResource(R.string.label_active_filters),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedCategory?.let { category ->
                            item("category") {
                                FilterChip(
                                    selected = true,
                                    onClick = { selectedCategory = null },
                                    label = { Text("${stringResource(R.string.label_category)}: ${category.displayName()}") },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.action_remove_filter)
                                        )
                                    }
                                )
                            }
                        }
                        if (onlyOverdue) {
                            item("overdue") {
                                FilterChip(
                                    selected = true,
                                    onClick = { onlyOverdue = false },
                                    label = { Text(stringResource(R.string.label_overdue_short)) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.action_remove_filter)
                                        )
                                    }
                                )
                            }
                        }
                        if (sortOption != SortOption.Newest) {
                            item("sort") {
                                FilterChip(
                                    selected = true,
                                    onClick = { sortOption = SortOption.Newest },
                                    label = {
                                        Text(
                                            when (sortOption) {
                                                SortOption.DueSoon -> stringResource(R.string.label_sort_due_short)
                                                SortOption.Alphabetical -> stringResource(R.string.label_sort_alpha_short)
                                                SortOption.Newest -> stringResource(R.string.label_sort_newest_short)
                                            }
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.action_remove_filter)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = layoutMode == LayoutMode.List,
                            onClick = { layoutMode = LayoutMode.List },
                            label = { Text(stringResource(R.string.layout_list)) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = layoutMode == LayoutMode.Board,
                            onClick = { layoutMode = LayoutMode.Board },
                            label = { Text(stringResource(R.string.layout_board_project)) }
                        )
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            delay(1000)
                            isRefreshing = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (filteredTodos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                                )
                                Text(
                                    text = if (searchQuery.isNotEmpty()) {
                                        stringResource(R.string.empty_no_results)
                                    } else if (selectedTab == BottomNavItem.Today) {
                                        stringResource(R.string.empty_no_today)
                                    } else if (selectedTab == BottomNavItem.Completed) {
                                        stringResource(R.string.empty_no_completed)
                                    } else {
                                        stringResource(R.string.empty_no_todos)
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                )
                                if (selectedTab == BottomNavItem.All && searchQuery.isEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = onOpenAddTodo,
                                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Text(stringResource(R.string.cta_first_todo))
                                    }
                                }
                            }
                        }
                    } else {
                        if (layoutMode == LayoutMode.List) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState
                            ) {
                                items(
                                    items = filteredTodos,
                                    key = { "${it.firebaseId}:${it.id}" }
                                ) { todo ->
                                    TodoItem(
                                        todo = todo,
                                        onToggleComplete = { viewModel.doneTodo(it) },
                                        onTogglePin = { viewModel.togglePinned(it) },
                                        onDelete = { deletedTodo ->
                                            viewModel.deleteTodo(deletedTodo)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = context.getString(
                                                        R.string.snackbar_deleted,
                                                        deletedTodo.title
                                                    ),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        },
                                        onClick = { onTodoClick(todo) }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState
                            ) {
                                boardSections.forEach { (project, tasks) ->
                                    item(key = "section-$project") {
                                        Text(
                                            text = project,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    items(
                                        items = tasks,
                                        key = { "${it.firebaseId}:${it.id}" }
                                    ) { todo ->
                                        TodoItem(
                                            todo = todo,
                                            onToggleComplete = { viewModel.doneTodo(it) },
                                            onTogglePin = { viewModel.togglePinned(it) },
                                            onDelete = { deletedTodo ->
                                                viewModel.deleteTodo(deletedTodo)
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = context.getString(
                                                            R.string.snackbar_deleted,
                                                            deletedTodo.title
                                                        ),
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            },
                                            onClick = { onTodoClick(todo) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    sheetState = filterSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.title_filters_sort),
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = stringResource(R.string.label_category),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(stringResource(R.string.label_all_categories)) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )

                        Category.entries.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category.displayName()) },
                                modifier = Modifier.heightIn(min = 48.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        FilterChip(
                            selected = onlyOverdue,
                            onClick = { onlyOverdue = !onlyOverdue },
                            label = { Text(stringResource(R.string.label_only_overdue)) },
                            modifier = Modifier.heightIn(min = 48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            )
                        )

                        if (suggestedProjects.isNotEmpty() || suggestedTags.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.title_quick_filters),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (suggestedProjects.isNotEmpty()) {
                                LazyRow(modifier = Modifier.fillMaxWidth()) {
                                    items(suggestedProjects, key = { it }) { project ->
                                        FilterChip(
                                            selected = false,
                                            onClick = {
                                                searchQuery = "project:${project.lowercase()}"
                                                showFilterSheet = false
                                            },
                                            label = { Text(project) },
                                            modifier = Modifier.padding(end = 8.dp, bottom = 6.dp)
                                        )
                                    }
                                }
                            }
                            if (suggestedTags.isNotEmpty()) {
                                LazyRow(modifier = Modifier.fillMaxWidth()) {
                                    items(suggestedTags, key = { it }) { tag ->
                                        FilterChip(
                                            selected = false,
                                            onClick = {
                                                searchQuery = "tag:$tag"
                                                showFilterSheet = false
                                            },
                                            label = { Text("#$tag") },
                                            modifier = Modifier.padding(end = 8.dp, bottom = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = stringResource(R.string.label_sorting),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FilterChip(
                            selected = sortOption == SortOption.Newest,
                            onClick = { sortOption = SortOption.Newest },
                            label = { Text(stringResource(R.string.sort_newest)) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                        FilterChip(
                            selected = sortOption == SortOption.DueSoon,
                            onClick = { sortOption = SortOption.DueSoon },
                            label = { Text(stringResource(R.string.sort_due_date)) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                        FilterChip(
                            selected = sortOption == SortOption.Alphabetical,
                            onClick = { sortOption = SortOption.Alphabetical },
                            label = { Text(stringResource(R.string.sort_alphabetic)) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )

                        Button(
                            onClick = {
                                selectedCategory = null
                                onlyOverdue = false
                                sortOption = SortOption.Newest
                            },
                            shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(stringResource(R.string.action_reset_filters))
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun RecentSearchesHeading(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.title_recent_searches),
        modifier = modifier.semantics { heading() },
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun matchesSearch(
    todo: Todo,
    query: String,
    context: android.content.Context
): Boolean {
    val normalized = query.trim().lowercase()
    if (normalized.isBlank()) return true

    val (plainTerms, operators) = parseSearchQuery(normalized)
    val categoryTitle = runCatching {
        categoryLabel(context, Category.valueOf(todo.category)).lowercase()
    }.getOrDefault("")
    val priorityTitle = runCatching {
        priorityLabel(context, Priority.valueOf(todo.priority)).lowercase()
    }.getOrDefault("")
    val statusText = if (todo.isCompleted) "tamamlandi completed" else "bekliyor pending"
    val dueDateText = todo.dueDate?.toString().orEmpty()
    val projectText = todo.project.lowercase()
    val todoTags = parseTags(todo.tags)

    val operatorsMatch = operators.all { (key, value) ->
        when (key) {
            "priority", "p" -> matchesPriorityOperator(todo, value, priorityTitle)
            "tag", "tags" -> matchesTagOperator(todoTags, value)
            "project", "proj" -> matchesProjectOperator(projectText, value)
            "category", "cat" -> matchesCategoryOperator(todo, value, categoryTitle)
            "due", "date" -> matchesDueOperator(todo, value)
            "status" -> matchesStatusOperator(todo, value)
            else -> true
        }
    }
    if (!operatorsMatch) return false

    if (plainTerms.isEmpty()) return true

    return plainTerms.all { term ->
        todo.title.lowercase().contains(term) ||
            todo.category.lowercase().contains(term) ||
            categoryTitle.contains(term) ||
            todo.priority.lowercase().contains(term) ||
            priorityTitle.contains(term) ||
            projectText.contains(term) ||
            todoTags.any { it.contains(term) } ||
            statusText.contains(term) ||
            dueDateText.contains(term)
    }
}

private fun parseSearchQuery(query: String): Pair<List<String>, List<Pair<String, String>>> {
    val plainTerms = mutableListOf<String>()
    val operators = mutableListOf<Pair<String, String>>()

    query.split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .forEach { token ->
            val operatorIndex = token.indexOf(':')
            if (operatorIndex <= 0 || operatorIndex == token.lastIndex) {
                plainTerms.add(token)
                return@forEach
            }
            val key = token.substring(0, operatorIndex).trim()
            val value = token.substring(operatorIndex + 1).trim()
            if (key.isBlank() || value.isBlank()) {
                plainTerms.add(token)
            } else {
                operators.add(key to value)
            }
        }

    return plainTerms to operators
}

private fun matchesPriorityOperator(todo: Todo, value: String, localizedPriority: String): Boolean {
    val normalizedValue = value.lowercase()
    val priorityRaw = todo.priority.lowercase()
    return when (normalizedValue) {
        "high", "yuksek" -> {
            priorityRaw == Priority.HIGH.name.lowercase() || localizedPriority.contains("yuksek")
        }
        "medium", "med", "orta" -> {
            priorityRaw == Priority.MEDIUM.name.lowercase() || localizedPriority.contains("orta")
        }
        "low", "dusuk" -> {
            priorityRaw == Priority.LOW.name.lowercase() || localizedPriority.contains("dusuk")
        }
        else -> priorityRaw.contains(normalizedValue) || localizedPriority.contains(normalizedValue)
    }
}

private fun matchesTagOperator(tags: List<String>, value: String): Boolean {
    val normalizedValue = normalizeTag(value)
    return tags.any { it.contains(normalizedValue) }
}

private fun matchesProjectOperator(project: String, value: String): Boolean {
    return project.contains(value.lowercase())
}

private fun matchesCategoryOperator(todo: Todo, value: String, localizedCategory: String): Boolean {
    val normalizedValue = value.lowercase()
    return todo.category.lowercase().contains(normalizedValue) ||
        localizedCategory.contains(normalizedValue)
}

private fun matchesStatusOperator(todo: Todo, value: String): Boolean {
    return when (value.lowercase()) {
        "completed", "done", "tamamlandi" -> todo.isCompleted
        "pending", "open", "bekliyor" -> !todo.isCompleted
        else -> true
    }
}

private fun matchesDueOperator(todo: Todo, value: String): Boolean {
    val dueDate = todo.dueDate ?: return value.lowercase() in listOf("none", "null", "yok")
    val now = System.currentTimeMillis()
    val todayStart = startOfDay(now)
    val tomorrowStart = todayStart + 24L * 60L * 60L * 1000L
    val dayAfterTomorrowStart = tomorrowStart + 24L * 60L * 60L * 1000L

    return when (value.lowercase()) {
        "today", "bugun" -> dueDate in todayStart until tomorrowStart
        "tomorrow", "yarin" -> dueDate in tomorrowStart until dayAfterTomorrowStart
        "overdue", "late", "gecikmis" -> dueDate < now && !todo.isCompleted
        "none", "null", "yok" -> false
        else -> dueDate.toString().contains(value)
    }
}

private fun normalizeTag(raw: String): String {
    return raw.trim().lowercase().removePrefix("#").replace(" ", "")
}

private fun parseTags(raw: String): List<String> {
    return raw.split(",")
        .map { normalizeTag(it) }
        .filter { it.isNotBlank() }
}

private fun startOfDay(timestamp: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun categoryLabel(context: android.content.Context, category: Category): String {
    return context.getString(
        when (category) {
            Category.WORK -> R.string.category_work
            Category.PERSONAL -> R.string.category_personal
            Category.SHOPPING -> R.string.category_shopping
            Category.HEALTH -> R.string.category_health
            Category.OTHER -> R.string.category_other
        }
    )
}

private fun priorityLabel(context: android.content.Context, priority: Priority): String {
    return context.getString(
        when (priority) {
            Priority.LOW -> R.string.priority_low
            Priority.MEDIUM -> R.string.priority_medium
            Priority.HIGH -> R.string.priority_high
        }
    )
}

private fun todayPriorityScore(todo: Todo, now: Long): Int {
    var score = 0

    if (!todo.isCompleted) {
        score += 10
    }
    if (todo.isPinned) {
        score += 100
    }

    val priorityScore = when (todo.priority) {
        Priority.HIGH.name -> 35
        Priority.MEDIUM.name -> 20
        Priority.LOW.name -> 8
        else -> 10
    }
    score += priorityScore

    todo.dueDate?.let { due ->
        val dayMs = 24L * 60L * 60L * 1000L
        when {
            due < now -> score += 55
            due <= now + dayMs -> score += 40
            due <= now + 2 * dayMs -> score += 20
            else -> score += 5
        }
    } ?: run {
        score += 6
    }

    if (todo.repeatIntervalDays > 0) {
        score += 6
    }
    if (todo.repeatRule != RepeatRule.NONE) {
        score += 10
    }

    val ageDays = ((now - todo.updatedAt).coerceAtLeast(0L) / (24L * 60L * 60L * 1000L)).toInt()
    score += ageDays.coerceAtMost(14)

    return score
}

private fun isTodayPriority(todo: Todo, now: Long): Boolean {
    if (todo.isCompleted) return false
    val todayStart = startOfDay(now)
    val tomorrowStart = todayStart + 24L * 60L * 60L * 1000L
    return todo.dueDate?.let { dueDate ->
        dueDate < tomorrowStart
    } ?: todo.isPinned
}
