package com.gh0u1l5.wechatmagician.spellbook.interfaces

import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop

interface IXmlParserHook {

    /**
     * Called when the XML parser is going to parse a XML string.
     *
     * @param xml the XML string given by the caller.
     * @param root the tag name of the section the caller want to parse.
     * @return to bypass the original method, return a MutableMap<String, String> object wrapped by
     * [Operation.replacement], or a throwable wrapped by [Operation.interruption], otherwise return
     * [Operation.nop].
     */
    fun onXmlParsing(xml: String, root: String): Operation<MutableMap<String, String>?> = nop()

    /**
     * Called when the XML parser has parsed a XML string.
     *
     * @param xml the XML string given by the caller.
     * @param root the tag name of the section the caller want to parse.
     * @param result the map generated from the XML string.
     */
    fun onXmlParsed(xml: String, root: String, result: MutableMap<String, String>) { }
}