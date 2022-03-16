import org.apache.commons.lang3.StringUtils
object Namer {

  def apply(number: Int, baseName: String): Namer = {
    new Namer(number, baseName)
  }
}


class Namer(number: Int, baseName: String) {

  val PATTERN = "xxxxxxxx"

  def name(): String = {
    def findDigitCount(): Int = {
      var digitCount = 1
      var tempNumber = number
      while (tempNumber / 10 > 1) {
        digitCount += 1
        tempNumber = tempNumber / 10
      }
      digitCount
    }
    val patternCount = PATTERN.length
    val sb = new StringBuilder
    val zeroCount = patternCount - findDigitCount()
    sb.append(baseName)
      .append(StringUtils.repeat("0", zeroCount))
      .append(number)
      .toString()
  }
}
