package dev.entao.app.jsonhttp


import dev.entao.app.http.HttpResult
import dev.entao.app.json.YsonArray
import dev.entao.app.json.YsonObject
import dev.entao.app.json.createYsonModel
import kotlin.reflect.KClass

/**
 * Created by entaoyang@163.com on 2018-04-07.
 */

val HttpResult.protoResult: ProtoResult get() = ProtoResult(this)
val HttpResult.ysonObject: YsonObject?
    get() {
        val s = this.valueText ?: return null
        return YsonObject(s)
    }

class ProtoResult(val httpResult: HttpResult) {
    var CodeOKValue = CODE_OK
    var CodeName = CODE
    var DataName = DATA
    var MsgName = MSG

    val jo: YsonObject = httpResult.ysonObject ?: YsonObject()
    val OK: Boolean
        get() {
            return httpResult.OK && code == CodeOKValue
        }

    val code: Int
        get() {
            return if (httpResult.OK) {
                jo.getInt(CodeName) ?: -1
            } else {
                httpResult.code
            }
        }

    val msg: String
        get() {
            return if (httpResult.OK) {
                jo.getString(MsgName) ?: ""
            } else {
                httpResult.errorMessage ?: "未知错误"
            }
        }

    val dataObject: YsonObject?
        get() {
            return jo.getObject(DataName)
        }

    val dataArray: YsonArray?
        get() {
            return jo.getArray(DataName)
        }
    val dataInt: Int?
        get() {
            return jo.getInt(DataName)
        }
    val dataLong: Long?
        get() {
            return jo.getLong(DataName)
        }
    val dataDouble: Double?
        get() {
            return jo.getReal(DataName)
        }
    val dataFloat: Float?
        get() {
            return jo.getReal(DataName)?.toFloat()
        }
    val dataString: String?
        get() {
            return jo.getString(DataName)
        }

    fun dataListObject(): ArrayList<YsonObject> {
        val ls = ArrayList<YsonObject>()
        if (OK) {
            val ar = this.dataArray ?: return ls
            ar.eachObject { yo ->
                ls.add(yo)
            }
        }
        return ls
    }

    inline fun <reified T : Any> dataListModel(cls: KClass<T>): ArrayList<T> {
        val ls = ArrayList<T>()
        if (OK) {
            val ar = this.dataArray ?: return ls
            ar.eachObject { yo ->
                val inst: T = cls.createYsonModel(yo)
                ls.add(inst)
            }
        }
        return ls
    }

    inline fun <reified T : Any> dataModel(cls: KClass<T>): T? {
        if (OK) {
            val yo: YsonObject = this.dataObject ?: return null
            return cls.createYsonModel(yo)
        }
        return null
    }

    override fun toString(): String {
        return jo.toString()
    }

    companion object {
        var CODE_OK = 0
        var CODE = "code"
        var DATA = "data"
        var MSG = "msg"

        var MSG_OK = "操作成功"
        var MSG_FAILED = "操作失败"

    }
}