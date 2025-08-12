package com.emreonal.eterationcase.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val tlFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.ROOT))

fun Double.toTl(): String = tlFormat.format(this) + " ₺"