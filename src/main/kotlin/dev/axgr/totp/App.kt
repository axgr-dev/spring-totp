package dev.axgr.totp

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.converter.BufferedImageHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.awt.image.BufferedImage
import java.util.*

@EnableScheduling
@SpringBootApplication
class App {

  companion object {
    private val log = LoggerFactory.getLogger(App::class.java)
  }

  private val secret = GoogleAuthenticator.createRandomSecret()

  init {
    log.info("Secret: $secret")
  }

  @Scheduled(fixedRate = 1_000L)
  fun ping() {
    val timestamp = Date(System.currentTimeMillis())
    val code = GoogleAuthenticator(secret).generate(timestamp)
    log.info("Code: $code")
  }

  @Bean
  fun qrCodeWriter() = QRCodeWriter()

  @Bean
  fun imageConverter(): HttpMessageConverter<BufferedImage> {
    return BufferedImageHttpMessageConverter()
  }

}

@Component
class CodeGenerator(private val writer: QRCodeWriter) {

  fun generate(issuer: String, email: String, secret: String): BufferedImage {
    val uri = "otpauth://totp/$issuer:$email?secret=$secret&issuer=$issuer"
    val matrix = writer.encode(uri, BarcodeFormat.QR_CODE, 200, 200)

    return MatrixToImageWriter.toBufferedImage(matrix)
  }

}

@RestController
class CodeController(private val generator: CodeGenerator) {

  @GetMapping("/code/{secret}", produces = [MediaType.IMAGE_PNG_VALUE])
  fun code(@PathVariable secret: String): BufferedImage {
    return generator.generate("Spring Boot MFA App", "hello@axgr.dev", secret)
  }

}

fun main(args: Array<String>) {
  runApplication<App>(*args)
}
