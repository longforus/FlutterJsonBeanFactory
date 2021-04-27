package com.ruiyu.file

import com.ruiyu.dart_to_helper.node.HelperFileGeneratorInfo

data class AllEntityInfoBean(val info: HelperFileGeneratorInfo,val import:String,var contentImport:String = "")
