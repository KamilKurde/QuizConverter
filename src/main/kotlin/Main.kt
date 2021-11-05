import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class Data(@SerialName("META") var meta: Meta, val Results: Map<String, Result>)

@Serializable
class Meta(
	@SerialName("Questions") val questions: Array<Question>,
	@Serializable
	val availableQuestions: Int = questions.size,
	@Serializable
	val isActive: Boolean = true,
	@Serializable
	val questionsVersion: Int = 1,
	@Serializable
	val wantedQuestions: Int = 20
)

@Serializable
class Question(val q: String, val a: String, val b: String, val c: String, val d: String)

@Serializable
class Result(val endTime: DateTime, val percent: Int, val score: Int, val serviceInfo: ServiceInfo, val studentClass: String? = null, val totalSeconds: Int, val timeInBackground: String? = null, val timesInBackground: Int? = null)

@Serializable
class DateTime(val date: String, val time: String)

@Serializable
class ServiceInfo(val apiLevel: Int, val deviceName: String, /*val questions: String,*/ val versionCode: Int)

private val json = Json {
	ignoreUnknownKeys = true
	encodeDefaults = true
}

fun main(args: Array<String>) {
	var fileName = if (args.isNotEmpty()) args[0] else ""
	while (!File(fileName).exists()) {
		print("Podaj lokalizację pliku wejściowego:")
		try {
			fileName = readLine()!!.replace("\"", "")
		} catch (e: Exception) {

		}
	}
	val inputFile = File(fileName)
	when (inputFile.absolutePath.split(".").last()) {
		"json" -> {
			val data = json.decodeFromString<Data>(inputFile.readLines().joinToString("\n"))
			val output = mutableListOf(arrayOf("Imię", "Nazwisko", "Klasa", "Wynik", "% Punktów", "Czas w sekundach", "Data ukończenia", "Czas ukończenia", "Czas aplikacji w tle", "Licznik chowania w tło", "Poziom API", "Nazwa urządzenia", "Wersja aplikacji").joinToString(";"))
			data.Results.forEach { (key, value) ->
				value.run {
					output += arrayOf(
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
					).map { it ?: "-" }.joinToString(";")
				}
			}
			ProcessBuilder("cmd.exe", "/C taskkill /IM EXCEL.exe /F")
				.start()
			Thread.sleep(1000L)
			val outputFile = File("wyniki.csv")
			outputFile.writeText(output.joinToString("\n"))
			println(outputFile.absolutePath)
			ProcessBuilder("cmd.exe", "/C start excel \"" + outputFile.absolutePath + "\"")
				.start()
		}
		"csv" -> {
			fun input(text: String): Int {
				while (true) {
					print(text)
					try {
						return readLine()!!.toInt()
					} catch (e: Exception) {
					}
				}
			}
			val version = input("Podaj wersję pytań: ")
			val wantedQuestions = input("Podaj ilość pytań która ma być zadana podczas quizu: ")
			val data = Data(
				Meta(
					inputFile.readLines().drop(1).map {
						val split = it.split(";")
						Question(
							split[0],
							split[1],
							split[2],
							split[3],
							split[4]
						)
					}.toTypedArray(),
					questionsVersion = version,
					wantedQuestions = wantedQuestions
				),
				emptyMap()
			)
			val outputFile = File("quiz.json")
			outputFile.writeText(json.encodeToString(data))
			println(outputFile.absolutePath)
		}
	}
}