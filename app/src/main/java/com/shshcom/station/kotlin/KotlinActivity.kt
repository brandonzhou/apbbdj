package com.shshcom.station.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mt.bbdj.R
import com.shshcom.module_base.network.Results
import com.shshcom.station.statistics.http.ApiPackageStatistic
import kotlinx.android.synthetic.main.activity_kotlin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class KotlinActivity : AppCompatActivity()  {

    val job = Job()
    val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + job)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        tvBtn.setOnClickListener {
            doClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun doClick(){
        presenterScope.launch {
            val result1 =  ApiPackageStatistic.todayExpressStatistics("12")
//                val result2 =  ApiPackageStatistic.queryExpressDetail("12","100101",1,1,0,1)
//            val result3 =  ApiPackageStatistic.totalStock("12",1)
//            val result4 =  ApiPackageStatistic.modifyMobile("12",142462, "18811321040")
//            val result5 =  ApiPackageStatistic.reSendSMSNotice("12",142462)

            val result = result1
            when(result){
                is Results.Success -> {
                    tvBtn.text = result.toString()
                }
                is Results.Failure -> {
                    tvBtn.text = result.error.message
                }
            }
        }
    }
}
