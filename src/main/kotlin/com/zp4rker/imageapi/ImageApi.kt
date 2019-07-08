package com.zp4rker.imageapi

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import javax.imageio.ImageIO

fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)
    server.createContext("/api", Handler)
    server.start()
}

object Handler : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) return

        println("request made")

        val requests = exchange.requestURI.toString().substring(5).split(",")
        val width = requests.find { it.split("=")[0].toLowerCase() == "width" }?.let { it.split("=")[1].toInt() } ?: 500
        val offset = (500 - width) / 2

        val image = ImageIO.read(this.javaClass.getResourceAsStream("/zaeem.png"))
        val canvas = BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB)
        canvas.createGraphics().run {
            color = Color.RED
            fillRect(0, 0, 500, 500)
            drawImage(image, offset, offset, width, width, null)
        }

        val bytes = ByteArrayOutputStream().also { ImageIO.write(canvas, "png", it) }.toByteArray()
        exchange.sendResponseHeaders(200, bytes.size.toLong())

        val output = exchange.responseBody
        output.write(bytes)
        output.close()
    }
}