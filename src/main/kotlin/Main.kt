import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

@Serializable
class Data(val Results: Map<String, Result>)

@Serializable
class Result(val endTime: DateTime, val percent: Int, val score: Int, val serviceInfo: ServiceInfo, val studentClass: String? = null, val totalSeconds: Int, val timeInBackground: String? = null, val timesInBackground: Int? = null)

@Serializable
class DateTime(val date: String, val time: String)

@Serializable
class ServiceInfo(val apiLevel: Int, val deviceName: String, /*val questions: String,*/ val versionCode: Int)

fun XSSFSheet.nextRow() = this.createRow(this.lastRowNum + 1)!!
fun XSSFRow.nextCell() = this.createCell((if (lastCellNum == (-1).toShort()) 0 else lastCellNum).toInt())!!
fun XSSFRow.setNextCell(value: Any) {
    val cell = nextCell()
    when (value) {
        is String -> cell.setCellValue(value)
        is Number -> cell.setCellValue(value.toDouble())
        is LocalDateTime -> cell.setCellValue(value)
        else -> throw IllegalArgumentException("Arguments of type " + value.javaClass.canonicalName + " are not supported")
    }
    cell.cellStyle = style
}

fun XSSFRow.addCells(vararg cells: Any?) = cells.forEach {
    if (it == null) {
        setNextCell("-")
    } else {
        setNextCell(it)
    }
}

private val json = Json { ignoreUnknownKeys = true }

val workBook = XSSFWorkbook()

val style = workBook.createCellStyle()!!

fun main(args: Array<String>) {
    val data = json.decodeFromString<Data>(File(args[0]).readLines().joinToString("\n"))
    val sheet = workBook.createSheet("wyniki")!!
    style.alignment = HorizontalAlignment.CENTER
    val headers = arrayOf("Imię", "Nazwisko", "Klasa", "Wynik", "% Punktów", "Czas w sekundach", "Data ukończenia", "Czas ukończenia", "Czas aplikacji w tle", "Licznik chowania w tło", "Poziom API", "Nazwa urządzenia", "Wersja aplikacji")
    sheet.nextRow().addCells(*headers)
    for (i in headers.indices) {
        sheet.setColumnWidth(i, 25 * 256)
    }
    data.Results.forEach { (key, value) ->
        value.run {
            sheet.nextRow().addCells(
                key.split(" ")[0],
                key.split(" ")[1],
                studentClass,
                score,
                percent,
                totalSeconds,
                endTime.date,
                endTime.time,
                timeInBackground,
                timesInBackground,
                serviceInfo.apiLevel,
                serviceInfo.deviceName,
                serviceInfo.versionCode
            )
        }
    }
    ProcessBuilder("cmd.exe", "/C taskkill /IM EXCEL.exe /F").start()
    Thread.sleep(1000L)
    val file = File("wyniki.xlsx")
    val fileOutputStream = FileOutputStream(file)
    workBook.write(fileOutputStream)
    fileOutputStream.close()
    println(file.absolutePath)
    ProcessBuilder("cmd.exe", "/C start excel \"" + file.absolutePath + "\"").start()
}