package com.microbond.app.core.navigation


sealed class NavigationScreen(val route: String){
    object Charts: NavigationScreen("chart_route")
    object Status: NavigationScreen("Status_route")
    object Communities: NavigationScreen("Communities_route")
    object Call: NavigationScreen("Call_route")
    object Profile : NavigationScreen("Profile_route")


    object  ChartRoom : NavigationScreen("chart_room_route/{contactedId}/{contactedName}"){
        fun CreateRoute(contactedId: String, contactedName: String): String{
            return "chart_room_route/$contactedId/$contactedName"
        }
        object Settings: NavigationScreen("settings_route")
    }

}
