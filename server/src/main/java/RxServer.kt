import io.reactivex.Observable
import java.awt.Desktop
import java.io.DataInputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URI

object RxServer {
    fun openSocket(serverPort: Int): Observable<ServerSocket> {
        return Observable.create<ServerSocket> {
            try {
                val serverSocket = ServerSocket(serverPort,
                        100, InetAddress.getByName("0.0.0.0"))
                it.onNext(serverSocket)
                println("Socket open")
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }
    }

    fun clientListener(serverSocket: ServerSocket): Observable<String> {
        return Observable.create {
            try {
                println("Wait a client")
                val socket = serverSocket.accept()
                val inputStream = socket.getInputStream()

                val dataInputStream = DataInputStream(inputStream)
                while (socket.isConnected) {
                    val clientLine = dataInputStream.readUTF()
                    it.onNext(clientLine)
                }
            } catch (e: Exception) {
                it.onError(e)
            }
            if (it.isDisposed) it.onComplete()
        }
    }

    fun openStream(clientLink: String): Observable<Boolean> {
        return Observable.create {
            try {
                val desktop = Desktop.getDesktop()
                val uri = URI(clientLink)

                desktop.browse(uri)
                it.onNext(true)
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }
    }
}