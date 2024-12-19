package com.saltwort.cotizadorgasto

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.saltwort.cotizadorgasto.database.AppDatabase
import com.saltwort.cotizadorgasto.database.EntityRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import kotlin.math.exp

class ActivityMain : AppCompatActivity() {

    private lateinit var btnReport: Button
    private lateinit var btnExpense: Button
    private lateinit var btnIncome: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonClear: Button

    private lateinit var editBalance : EditText
    private lateinit var editDetail : EditText

    private lateinit var reclyclerView : RecyclerView

    private lateinit var textValueIncome : TextView
    private lateinit var textValueExpense : TextView
    private lateinit var textValueTotal : TextView
    private lateinit var textDate : TextView
    private lateinit var buttonDecrease : Button
    private lateinit var buttonIncrease : Button

    private val INCOME : String = "+"
    private val EXPENSE : String = "-"
    private val REPORT : String = "*"

    private var sign : String = INCOME

    private lateinit var currentDate : Date
    private lateinit var calendar : Calendar
    private lateinit var db: AppDatabase
    private lateinit var listRv: List<EntityRecord>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById()
        initialDatabase()
        setAdapter()
    }

    private fun initialDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-expense-quoter"
        ).build()
    }

    private fun findViewById() {
        calendar = Calendar.getInstance()
        //calendar.add(Calendar.MONTH, -1)
        currentDate = calendar.time
        this.buttonsHome()
        this.addRecord()
        this.showRecords()
    }

    private fun showRecords() {
        this.reclyclerView = findViewById(R.id.reclyclerView)
        this.textDate = findViewById(R.id.textDate)
        this.buttonIncrease = findViewById(R.id.buttonIncrease)
        this.buttonDecrease = findViewById(R.id.buttonDecrease)
        this.textValueIncome = findViewById(R.id.textValueIncome)
        this.textValueExpense = findViewById(R.id.textValueExpense)
        this.textValueTotal = findViewById(R.id.textValueBalance)
        actionDate("")
        this.buttonIncrease.setOnClickListener { actionDate("+") }
        this.buttonDecrease.setOnClickListener { actionDate("-") }
    }

    private fun clearRecord() {
        editBalance.text.clear()
        editDetail.text.clear()
    }

    private fun saveRecord() {
        if (validation()) {
            val entity = this.getEntity()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    db.recordDao().insertAll(entity)
                    withContext(Dispatchers.Main){
                        reclyclerView.adapter = Adapter(listRv)
                        showToastSaved("Registro guardado exitosamente")
                        clearRecord()
                    }
                } catch (e : Exception) {
                    showToastSaved("Error al intentar salvar registro" + e.toString())
                }
            }
        }
    }

    private fun showToastSaved(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getEntity() : EntityRecord{
        val entity = EntityRecord()
        entity.balance = editBalance.text.toString().toFloat()
        entity.sign = sign
        entity.date = getCurrentDate()
        entity.detail = editDetail.text.toString()
        return entity
    }

    private fun getCurrentDate(): String {
        val date = calendar.time
        return convertDateToStr(date, "yyyy-MM-dd")
    }

    private fun validation(): Boolean {
        return editBalance.text.isNotEmpty()
    }

    private fun addRecord() {
        this.editBalance = findViewById(R.id.editBalance)
        this.editDetail = findViewById(R.id.editDetail)
        this.buttonSave = findViewById(R.id.buttonSave)
        this.buttonClear = findViewById(R.id.buttonClear)

        buttonSave.setOnClickListener { saveRecord() }
        buttonClear.setOnClickListener { clearRecord() }
    }

    private fun buttonsHome() {
        btnIncome = findViewById(R.id.btnIncome)
        btnExpense = findViewById(R.id.btnExpense)
        btnReport = findViewById(R.id.btnReport)

        btnIncome.setOnClickListener { setInteractionView(INCOME) }
        btnExpense.setOnClickListener { setInteractionView(EXPENSE) }
        btnReport.setOnClickListener { setInteractionView(REPORT) }
    }

    private fun setInteractionView(action: String) {
        this.inactiveViews()
        when (action) {
            INCOME -> {
                actionLayout(findViewById(R.id.lineardIncomeExpense), "+", View.VISIBLE)
            }
            EXPENSE -> {
                actionLayout(findViewById(R.id.lineardIncomeExpense), "-", View.VISIBLE)
            }
            else -> {
                setAdapter()
                actionLayout(findViewById(R.id.layoutReport), "", View.VISIBLE)
            }
        }
    }

    private fun setAdapter() {
        reclyclerView.layoutManager = LinearLayoutManager(this)
        listRv = ArrayList()
        val calendar : Calendar = getCalendar()

        val startDate = convertDateToStr(calendar.time, "yyyy-MM-dd")

        calendar.add(Calendar.MONTH, 1) // Ir al próximo mes
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Primer día del próximo mes
        calendar.add(Calendar.SECOND, -1) // Un segundo antes del próximo mes

        val endDate: String = convertDateToStr(calendar.time, "yyyy-MM-dd")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                listRv = db.recordDao().getRecordsByDateRange(startDate, endDate)
                withContext(Dispatchers.Main){
                    reclyclerView.adapter = Adapter(listRv)
                    setBalance()
                }
            }catch (e : Exception) {
                println(e)
            }

        }
    }

    private fun setBalance() {
        println("*********************************")
        println(listRv.size)
        val income = listRv.filter { it.sign == "+" }.map { it.balance }.sum()
        val expense = listRv.filter { it.sign == "-" }.map { it.balance }.sum()
        val balance = income - expense
        textValueIncome.text = income.toString()
        textValueExpense.text = expense.toString()
        textValueTotal.text = balance.toString()
    }


    private fun getCalendar(): Calendar {
        // Crear un calendario para el inicio y fin del mes
        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        // Configurar la fecha de inicio del mes
        // Configurar inicio del mes
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Primer día del mes
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }


    private fun actionLayout(view: View, sign: String, visible : Int) {
        view.visibility = visible
        this.sign = sign
    }

    private fun inactiveViews() {
        actionLayout(findViewById(R.id.lineardIncomeExpense), "", View.INVISIBLE)
        actionLayout(findViewById(R.id.layoutReport), "", View.INVISIBLE)
    }

    /******************************** SECTION DATE ***********************************************/

    private fun actionDate(action: String) {
        if (action == INCOME) {
            calendar.add(Calendar.MONTH, 1)
            this.currentDate = calendar.time
        } else  if(action == EXPENSE) {
            calendar.add(Calendar.MONTH, -1)
            this.currentDate = calendar.time
        }
        setDateText()
        setAdapter()
    }

    private fun setDateText() {
        textDate.text = convertDateToStr(currentDate, "MMMM yyyy")
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToStr(date : Date, format : String): String {
        return SimpleDateFormat(format).format(date)
    }
}