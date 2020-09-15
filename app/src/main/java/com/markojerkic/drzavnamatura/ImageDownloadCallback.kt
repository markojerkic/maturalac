package com.markojerkic.drzavnamatura


interface ImageDownloadCallback {
    fun positiveCallBack(byteArray: ByteArray)
    fun negativeCallBack()
}