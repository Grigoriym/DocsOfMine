package com.grappim.docuvault.ui.screens.docs.add

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grappim.docuvault.uikit.theme.DefaultArrangement
import com.grappim.docuvault.uikit.theme.DefaultHorizontalPadding
import com.grappim.docuvault.uikit.theme.DocuVaultTheme
import com.grappim.docuvault.uikit.widget.DomButtonDefault
import com.grappim.docuvault.uikit.widget.DomGroupItem
import com.grappim.docuvault.uikit.widget.PlatoSnackbar
import com.grappim.docuvault.uikit.widget.PlatoTextFieldDefault
import com.grappim.docuvault.utils.PlatoFileItem
import com.grappim.docuvault.utils.LaunchedEffectResult
import com.grappim.docuvault.utils.NativeText
import com.grappim.docuvault.utils.asString
import com.grappim.docuvault.utils.files.CameraTakePictureData
import com.grappim.docuvault.utils.files.FileData
import com.grappim.docuvault.utils.files.mime.MimeTypes
import com.grappim.domain.model.group.Group

@Composable
fun AddDocumentScreen(
    viewModel: AddDocumentViewModel = hiltViewModel(),
    onDocumentCreated: () -> Unit,
    goBack: () -> Unit
) {
    var backEnabled by remember {
        mutableStateOf(true)
    }
    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    var cameraTakePictureData by remember {
        mutableStateOf(CameraTakePictureData.empty())
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        uris.forEach {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        viewModel.addDocumentsFromGallery(uris)
    }
    val filesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        viewModel.addDocuments(uris)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            viewModel.addCameraPicture(cameraTakePictureData)
        }
    }

    val snackBarMessage by viewModel.snackBarMessage.collectAsState(
        initial = LaunchedEffectResult(
            data = NativeText.Empty,
            timestamp = 0L
        )
    )

    LaunchedEffect(snackBarMessage) {
        if (snackBarMessage.data !is NativeText.Empty) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = snackBarMessage.data.asString(context)
            )
        }
    }

    val groups by viewModel.groups.collectAsState()
    val files = remember {
        viewModel.filesUris
    }
    val documentName by viewModel.documentName.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val documentCreated by viewModel.documentCreated.collectAsState()

    if (documentCreated) {
        onDocumentCreated()
    }

    BackHandler(
        enabled = backEnabled
    ) {
        if (files.isNotEmpty()) {
            showAlertDialog = true
        } else {
            backEnabled = false
            viewModel.removeData()
            goBack()
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = {
                showAlertDialog = false
            },
            title = {
                Text(text = "Save the changes before exit?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlertDialog = false
                        backEnabled = false
                        viewModel.saveData()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAlertDialog = false
                        backEnabled = false
                        viewModel.removeData()
                        goBack()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    AddDocumentScreenContent(
        documentName = documentName,
        setDocumentName = {
            viewModel.setDocumentName(it)
        },
        onGalleryClick = {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        onCameraClick = {
            cameraTakePictureData = viewModel.getCameraImageFileUri()
            cameraLauncher.launch(cameraTakePictureData.uri)
        },
        groups = groups,
        onCreateClick = {
            viewModel.createDocument()
        },
        filesUris = files,
        onFilesClick = {
            filesLauncher.launch(MimeTypes.mimeTypesForDocumentPicker)
        },
        onGroupClick = {
            viewModel.setGroup(it)
        },
        selectedGroup = selectedGroup,
        scaffoldState = scaffoldState,
        onFileRemoved = {
            viewModel.removeFile(it)
        }
    )
}

@Composable
private fun AddDocumentScreenContent(
    documentName: String,
    setDocumentName: (String) -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    groups: List<Group>,
    onCreateClick: () -> Unit,
    filesUris: List<FileData>,
    onFilesClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    selectedGroup: Group?,
    scaffoldState: ScaffoldState,
    onFileRemoved: (FileData) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                PlatoSnackbar(
                    snackbarData = data
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Create new document",
                    modifier = Modifier
                        .padding(horizontal = DefaultHorizontalPadding)
                )

                PlatoTextFieldDefault(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = DefaultHorizontalPadding),
                    value = documentName,
                    onValueChange = setDocumentName,
                    label = "Name"
                )
                GroupsContent(
                    groups = groups,
                    onGroupClick = onGroupClick,
                    selectedGroup = selectedGroup
                )

                AddFromContent(
                    onGalleryClick = onGalleryClick,
                    onCameraClick = onCameraClick,
                    onFilesClick = onFilesClick
                )
                FilesInfoContent(
                    filesUris = filesUris
                )
                AddedFilesContent(
                    filesUris = filesUris,
                    onFileRemoved = onFileRemoved
                )
            }

            DomButtonDefault(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 16.dp
                    )
                    .padding(horizontal = DefaultHorizontalPadding),
                text = "Create"
            )
        }
    }
}

@Composable
private fun FilesInfoContent(filesUris: List<FileData>) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = DefaultHorizontalPadding)
    ) {
        Text(text = "Files: ${filesUris.size}")
    }
}

@Composable
private fun GroupsContent(
    groups: List<Group>,
    onGroupClick: (Group) -> Unit,
    selectedGroup: Group?
) {
    Text(
        text = "Select Group",
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = DefaultHorizontalPadding)
    )
    LazyRow(
        modifier = Modifier
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(DefaultArrangement),
        contentPadding = PaddingValues(start = 8.dp)
    ) {
        items(groups) { group ->
            DomGroupItem(
                group = group,
                onGroupClick = onGroupClick,
                isSelected = group.id == selectedGroup?.id
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun AddedFilesContent(filesUris: List<FileData>, onFileRemoved: (FileData) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 8.dp
        )
    ) {
        items(
            items = filesUris,
            key = { uris ->
                uris.uri
            }
        ) { uri ->
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onFileRemoved(uri)
            }
            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color by animateColorAsState(
                        if (dismissState.targetValue == DismissValue.Default) {
                            Color.White
                        } else {
                            Color.Red
                        },
                        label = ""
                    )
                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                        label = ""
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (dismissState.targetValue == DismissValue.DismissedToStart) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier.scale(scale)
                            )
                        }
                    }
                }
            ) {
                PlatoFileItem(
                    fileData = uri,
                    onFileClicked = {}
                )
            }
        }
    }
}

@Composable
private fun AddFromContent(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFilesClick: () -> Unit
) {
    Text(
        text = "Add from:",
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = DefaultHorizontalPadding)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DefaultHorizontalPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onCameraClick
        ) {
            Text(text = "Camera")
        }
        Button(
            onClick = onGalleryClick
        ) {
            Text(text = "Gallery")
        }
        Button(
            onClick = onFilesClick
        ) {
            Text(text = "Files")
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
private fun AddDocumentScreenContentPreview() {
    DocuVaultTheme {
        AddDocumentScreenContent(
            documentName = "",
            setDocumentName = {},
            onGalleryClick = {},
            onCameraClick = {},
            groups = Group.getGroupsForPreview(),
            onCreateClick = {},
            filesUris = emptyList(),
            onFilesClick = {},
            onGroupClick = {},
            selectedGroup = Group.getGroupForPreview(),
            scaffoldState = rememberScaffoldState(),
            onFileRemoved = {}
        )
    }
}