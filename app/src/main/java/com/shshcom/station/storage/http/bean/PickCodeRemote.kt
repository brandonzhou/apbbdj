package com.shshcom.station.storage.http.bean


import com.google.gson.annotations.SerializedName
import com.mt.bbdj.baseconfig.db.PickupCode

data class PickCodeRemote(
        @SerializedName("number")
        val number: Int, // 1000
        @SerializedName("rule")
        val rule: Int, // 1
        @SerializedName("shelves_id")
        val shelvesId: Int, // 1
        @SerializedName("shelves_name")
        val shelvesName: String,
        @SerializedName("time")
        val time: Long, // 111111111111111111
        @SerializedName("last_code")
        val lastCode: String
) {
    companion object {
        fun from(pickupCode: PickupCode): PickCodeRemote {
            val rule = PickupCode.Type.from(pickupCode.type).rule

            val pickCodeRemote = PickCodeRemote(pickupCode.startNumber, rule, pickupCode.shelfId,
                    pickupCode.shelfNumber, pickupCode.time, pickupCode.lastCode)
            return pickCodeRemote
        }
    }

}
