package com.example.visual_programming2.supp

import android.telephony.*
import android.util.Log

object CellDataCollector {
    private val log_tag = "CELL_COLLECTOR"

    fun collectCellInfo(telephonyManager: TelephonyManager): String {
        val result = StringBuilder()

        try {
            val cellInfoList = telephonyManager.allCellInfo

            if (cellInfoList != null && cellInfoList.isNotEmpty()) {
                for (i in 0 until cellInfoList.size) {
                    val info = cellInfoList[i]

                    when (info) {
                        is CellInfoLte -> collectLteInfo(info, result, i)
                        is CellInfoGsm -> collectGsmInfo(info, result, i)
                        is CellInfoNr -> collectNrInfo(info, result, i)
                    }
                }
            } else {
                result.append("NO_CELL_INFO\n")
            }
        } catch (e: Exception) {
            Log.e(log_tag, "Ошибка сбора данных: ${e.message}")
            result.append("CELL_INFO_ERROR: ${e.message}\n")
        }
        return result.toString()
    }

    private fun collectLteInfo(info: CellInfoLte, result: StringBuilder, index: Int) {
        result.append("LTE CELL #${index+1}\n")
        val cellId = info.cellIdentity as CellIdentityLte
        result.append("TYPE=LTE\n")
        result.append("BAND=${cellId.bandwidth}\n")
        result.append("CELL_ID=${cellId.ci}\n")
        result.append("EARFCN=${cellId.earfcn}\n")
        result.append("MCC=${cellId.mccString}\n")
        result.append("MNC=${cellId.mncString}\n")
        result.append("PCI=${cellId.pci}\n")
        result.append("TAC=${cellId.tac}\n")

        val signal = info.cellSignalStrength as CellSignalStrengthLte
        result.append("ASU_LEVEL=${signal.asuLevel}\n")
        result.append("CQI=${signal.cqi}\n")
        result.append("RSRP=${signal.rsrp}\n")
        result.append("RSRQ=${signal.rsrq}\n")
        result.append("RSSI=${signal.rssi}\n")
        result.append("RSSNR=${signal.rssnr}\n")
        result.append("TIMING_ADVANCE=${signal.timingAdvance}\n")
    }
    private fun collectGsmInfo(info: CellInfoGsm, result: StringBuilder, index: Int) {
        result.append("GSM CELL #${index+1} \n")

        val cellId = info.cellIdentity as CellIdentityGsm
        result.append("TYPE=GSM\n")
        result.append("CELL_ID=${cellId.cid}\n")
        result.append("BSIC=${cellId.bsic}\n")
        result.append("ARFCN=${cellId.arfcn}\n")
        result.append("LAC=${cellId.lac}\n")
        result.append("MCC=${cellId.mccString}\n")
        result.append("MNC=${cellId.mncString}\n")
        result.append("PSC=${cellId.psc}\n")

        val signal = info.cellSignalStrength as CellSignalStrengthGsm
        result.append("DBM=${signal.dbm}\n")
        result.append("RSSI=${signal.rssi}\n")
        result.append("TIMING_ADVANCE=${signal.timingAdvance}\n")
    }

    private fun collectNrInfo(info: CellInfoNr, result: StringBuilder, index: Int) {
        result.append("5G CELL #${index+1}\\n")

        val cellId = info.cellIdentity as CellIdentityNr
        result.append("TYPE=5G\n")
        result.append("BANDS=${cellId.bands?.contentToString()}\n")
        result.append("NCI=${cellId.nci}\n")
        result.append("PCI=${cellId.pci}\n")
        result.append("NRARFCN=${cellId.nrarfcn}\n")
        result.append("TAC=${cellId.tac}\n")
        result.append("MCC=${cellId.mccString}\n")
        result.append("MNC=${cellId.mncString}\n")

        val signal = info.cellSignalStrength as CellSignalStrengthNr
        result.append("SS_RSRP=${signal.ssRsrp}\n")
        result.append("SS_RSRQ=${signal.ssRsrq}\n")
        result.append("SS_SINR=${signal.ssSinr}\n")
        result.append("TIMING_ADVANCE=${signal.timingAdvanceMicros}\n")
    }
}