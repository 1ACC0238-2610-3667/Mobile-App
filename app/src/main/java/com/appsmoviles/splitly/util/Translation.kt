package com.appsmoviles.splitly.utils

import androidx.compose.runtime.staticCompositionLocalOf

object Translations {
    val en = mapOf(
        "welcome_back" to "Welcome Back",
        "manage_household" to "Manage your household clearly",
        "primary_household" to "Primary Household",
        "total_members" to "Total Members",
        "total_expenses" to "Total Expenses",
        "total_contributions" to "Total Contributions",
        "manage_members_title" to "Manage Members",
        "manage_members_subtitle" to "Manage household members",
        "manage_expenses_title" to "Manage Expenses",
        "manage_expenses_subtitle" to "Manage household expenses",
        "manage_contributions_title" to "Manage Contributions",
        "manage_contributions_subtitle" to "Manage household contributions",
        "manage_households_title" to "Manage Households",
        "manage_households_subtitle" to "Create and manage your households"
    )

    val es = mapOf(
        "welcome_back" to "Bienvenido de nuevo",
        "manage_household" to "Administra tu hogar con claridad",
        "primary_household" to "Hogar Primario",
        "total_members" to "Total Miembros",
        "total_expenses" to "Gastos Totales",
        "total_contributions" to "Aportes Totales",
        "manage_members_title" to "Gestionar Miembros",
        "manage_members_subtitle" to "Administra los miembros del hogar",
        "manage_expenses_title" to "Gestionar Gastos",
        "manage_expenses_subtitle" to "Administra los gastos del hogar",
        "manage_contributions_title" to "Gestionar Aportes",
        "manage_contributions_subtitle" to "Administra los aportes del hogar",
        "manage_households_title" to "Gestionar Hogares",
        "manage_households_subtitle" to "Crea y administra tus hogares"
    )
}

val LocalTranslations = staticCompositionLocalOf { Translations.es }