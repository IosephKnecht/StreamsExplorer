package com.project.iosephknecht.streamsexplorer

import io.reactivex.Observable
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.URI


object RxRequest {
    fun openSocket(wifiAddress: String, serverPort: Int): Observable<Socket> {
        return Observable.create<Socket> {
            try {
                //val ipAddress = InetAddress.getByName("192.168.137.159")
                val ipAddress = InetAddress.getByName(wifiAddress)
                val socket = Socket(ipAddress, serverPort)
                it.onNext(socket)
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onComplete()
        }
    }

    fun postLink(link: URI, socket: Socket): Observable<String> {
        return Observable.create {
            try {
                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.writeUTF(link.toString())
                dataOutputStream.flush()
                it.onNext("Success...")
            } catch (e: Exception) {
                it.onError(Throwable(e))
            } finally {
                socket.close()
            }
            it.onComplete()
        }
    }
}