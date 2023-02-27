package com.Sanleone.progress.menuHandler

import android.view.Menu
import android.view.MenuItem

object MenuHandler {

    fun ShowElementsInMenu(menu: Menu, ids: MutableList<Int>){
        for (i in 0 until menu.size()){
            val item = menu.getItem(i);
            item.isVisible = ids.contains(item.itemId)
        }
    }

    fun GetMenuItemById(menu: Menu, id: Int): MenuItem?{
        for (i in 0 until menu.size()){
            val item = menu.getItem(i)
            if (item.itemId == id){
                return item
            }
        }
        return null
    }

}