package com.example.shoplist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ShopListMenu() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        var sList by remember { mutableStateOf(listOf<ShoppingItem>()) }
        var showAddDialog by remember { mutableStateOf(false) }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(text = "Add new item")
        }

        if (showAddDialog) {
            AddItemAlertDialog(
                listSize = sList.size,
                onAddItem = { sList = sList + it },
                onChangeShowStatus = { showAddDialog = false })

        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        )
        {
            items(sList) { item ->
                if (item.isEditing) {
                    ShopListItemEditor(
                        item = item,
                        onEditComplete = { editedName, editedQuantity ->
                            sList = sList.map { it.copy( isEditing = false ) }
                            val editedItem = sList.find{ it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        }
                    )
                } else {
                    ShopListItem(item = item,
                        onEditClick = {
                            sList = sList.map { it.copy( isEditing = it.id == item.id  ) }
                        },
                        onDeleteClick = {
                            sList = sList - item
                        })
                }
            }
        }

    }
}

@Composable
fun AddItemAlertDialog(
    listSize: Int,
    onAddItem: (item: ShoppingItem) -> Unit,
    onChangeShowStatus: () -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = { onChangeShowStatus() },
        title = { Text(text = "Add Shopping Item")},
        text = {
            Column {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = {itemName = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = itemQuantity,
                    onValueChange = {itemQuantity = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
                        val item = ShoppingItem (
                            id = listSize + 1,
                            name = itemName,
                            quantity = itemQuantity.toInt()
                        )

                        onAddItem(item)
                        onChangeShowStatus()
                    }
                }) {
                    Text(text = "Add")
                }

                Button(onClick = { onChangeShowStatus() }) {
                    Text(text = "Cancel")
                }
            }
        }
    )
}

@Composable
fun ShopListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0xFF6650a4)),
                shape = RoundedCornerShape(20.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = item.name,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Qty:" + item.quantity,
            modifier = Modifier.padding(8.dp)
        )

        Row (
            modifier = Modifier.padding(8.dp)
        ) {

            IconButton(onClick = { onEditClick() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }

            IconButton(onClick = { onDeleteClick() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

        }
    }
}

@Composable
fun ShopListItemEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
    ) {

        Column {

            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp))

            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp))

        }

        Button(
            onClick = {
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            }) {
            Text(text = "Save")
        }

    }

}

data class ShoppingItem (
    var id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

