package com.example.ui.components

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RoyalPurple
import java.util.Calendar

@Composable
fun DateTimePicker(
    onDateTimeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                try {
                    if (context is Activity && (context.isFinishing || context.isDestroyed)) {
                        return@OutlinedButton
                    }

                    // Always get fresh current time when opening the picker
                    val nowCalendar = Calendar.getInstance()
                    val selectedCalendar = Calendar.getInstance()

                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            selectedCalendar.set(Calendar.YEAR, year)
                            selectedCalendar.set(Calendar.MONTH, month)
                            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            // Post to main looper to ensure DatePickerDialog is cleanly dismissed
                            Handler(Looper.getMainLooper()).post {
                                try {
                                    if (context is Activity && (context.isFinishing || context.isDestroyed)) {
                                        return@post
                                    }

                                    val currentHour = nowCalendar.get(Calendar.HOUR_OF_DAY)
                                    val currentMinute = nowCalendar.get(Calendar.MINUTE)

                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            selectedCalendar.set(Calendar.MINUTE, minute)
                                            selectedCalendar.set(Calendar.SECOND, 0)
                                            selectedCalendar.set(Calendar.MILLISECOND, 0)

                                            val chosenTimeMillis = selectedCalendar.timeInMillis
                                            val currentTimeMillis = System.currentTimeMillis()

                                            // Validate that chosen time is strictly in the future
                                            if (chosenTimeMillis <= currentTimeMillis) {
                                                Toast.makeText(
                                                    context,
                                                    "שגיאה: המועד שנבחר כבר עבר! יש לבחור תאריך ושעה עתידיים.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                return@TimePickerDialog
                                            }

                                            // Deliver the safe future timestamp
                                            onDateTimeSelected(chosenTimeMillis)
                                        },
                                        currentHour,
                                        currentMinute,
                                        true
                                    )
                                    timePickerDialog.setTitle("בחר שעת הפצה")
                                    timePickerDialog.show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "שגיאה בפתיחת שעון: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        nowCalendar.get(Calendar.YEAR),
                        nowCalendar.get(Calendar.MONTH),
                        nowCalendar.get(Calendar.DAY_OF_MONTH)
                    )

                    // Do not allow picking past dates
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000L
                    datePickerDialog.setTitle("בחר תאריך עתידי")
                    datePickerDialog.show()
                } catch (e: Exception) {
                    Toast.makeText(context, "שגיאה בפתיחת לוח שנה: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = RoyalPurple
            ),
            modifier = Modifier.testTag("date_time_picker_button")
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = RoyalPurple
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "בחר תאריך ושעה עתידיים ⏰",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
