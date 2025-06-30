package com.example.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp

data class ShoppingItems(
    val id : Int,
    var name : String,
    var quantity : Int,
    var isEditing : Boolean = false
)

@Composable
fun ShoppingItemEditor (item: ShoppingItems, onEditComplete : (String, Int) -> Unit){ // Changed here
    var editedName by remember {mutableStateOf(item.name)}
    var editedQuantity by remember {mutableStateOf(item.quantity.toString())}
    var isEditing by remember {mutableStateOf(item.isEditing)}

    Row (modifier = Modifier.fillMaxWidth().padding(8.dp).background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly){
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
            Button(
                onClick = {
                    isEditing = false // This state variable seems local and doesn't affect the parent
                    onEditComplete(editedName,editedQuantity.toIntOrNull() ?: 1)
                }
            ){
                Text("Save")
            }
        }
    }
}





@Composable
fun ShoppingListApp (){

//    var sItems by remember { mutableStateOf(listOf<ShoppingItems>()) }

    var sItems by remember { mutableStateOf(listOf<ShoppingItems>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("")}
    var itemQuantity by remember { mutableStateOf("")}


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center)
    {
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {showDialog = true},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)) {
            Text("Add Item")
        }
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp))
        {
            items (sItems){
                item ->
                if (item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName, editedQuantity ->
                        sItems = sItems.map { it.copy(isEditing = false) }
                        val editedItem = sItems.find { it.id == item.id }
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    } )
                }
                else {
                    ShoppingListItems(item= item,
                        onEditClick = {
                        // finding out which item we are editing and changing "isEditing Boolean true".
                        sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                    }, onDeleteClick = {
                        sItems = sItems - item
                        })
                }
            }
        }
    }

    if (showDialog){
        AlertDialog( onDismissRequest = {showDialog = false},
            confirmButton = {
                Row (modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        showDialog = false
                    } ) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        if(itemName.isNotBlank()){
                            var newItem = ShoppingItems(
                                id = sItems.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt()
                            )
                            sItems = sItems + newItem
                            showDialog = false
                            itemName = ""
                        }
                    } ) {
                        Text("Add")
                    }

                }
            },
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = {itemName=it},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = {itemQuantity=it},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )

                }
            })
    }
}


@Composable
fun ShoppingListItems (
    item : ShoppingItems,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp).border(
        border = BorderStroke(2.dp, color = Color.Gray),
        shape = RoundedCornerShape(20),
    ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(item.name, modifier = Modifier.padding(8.dp))
        Text("Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            //Edit Button
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit , contentDescription = null)
            }
            //Delete Button
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete , contentDescription = null)
            }
        }
    }
}