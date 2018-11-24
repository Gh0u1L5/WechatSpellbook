package com.gh0u1l5.wechatmagician.spellbook.parser

/**
 * Dex 格式的文件头
 *
 * Refer: https://source.android.com/devices/tech/dalvik/dex-format
 */
@ExperimentalUnsignedTypes
class DexHeader {
    var version: Int = 0

    var checksum: UInt = 0u

    var signature: ByteArray = ByteArray(kSHA1DigestLen)

    var fileSize: UInt = 0u

    var headerSize: UInt = 0u

    var endianTag: UInt = 0u
    
    var linkSize: UInt = 0u
    
    var linkOff: UInt = 0u
    
    var mapOff: UInt = 0u
    
    var stringIdsSize: Int = 0
    
    var stringIdsOff: UInt = 0u
    
    var typeIdsSize: Int = 0
    
    var typeIdsOff: UInt = 0u
    
    var protoIdsSize: Int = 0
    
    var protoIdsOff: UInt = 0u
    
    var fieldIdsSize: Int = 0
    
    var fieldIdsOff: UInt = 0u
    
    var methodIdsSize: Int = 0
    
    var methodIdsOff: UInt = 0u
    
    var classDefsSize: Int = 0
    
    var classDefsOff: UInt = 0u
    
    var dataSize: Int = 0
    
    var dataOff: UInt = 0u

    /**
     * @suppress
     */
    companion object {
        const val kSHA1DigestLen = 20
        const val kSHA1DigestOutputLen = kSHA1DigestLen * 2 + 1
    }
}