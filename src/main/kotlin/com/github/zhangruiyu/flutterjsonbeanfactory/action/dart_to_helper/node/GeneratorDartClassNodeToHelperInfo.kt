package com.github.zhangruiyu.flutterjsonbeanfactory.action.dart_to_helper.node

import com.github.zhangruiyu.flutterjsonbeanfactory.action.dart_to_helper.model.FieldClassTypeInfo
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.CompositeElement
import com.jetbrains.lang.dart.DartElementType
import com.jetbrains.lang.dart.DartTokenTypes
import com.jetbrains.lang.dart.psi.DartFile
import org.jetbrains.kotlin.psi.psiUtil.children


object GeneratorDartClassNodeToHelperInfo {
    val notSupportType = listOf("static", "const")
    fun getDartFileHelperClassGeneratorInfo(file: PsiFile): HelperFileGeneratorInfo? {
        val dartFile = file as DartFile
// 查找顶级类定义
        // 查找顶级类定义


        //不包含JsonConvert 那么就不转
        return if (file.text.contains("@JsonSerializable") && file.name != "json_convert_content.dart") {
            val classDefinition = dartFile.children
            /*  classDefinition.forEach {
  //                println(it)
                  if (it is DartClass) {
                      val children = it.children
                      children.forEach { itemClass ->
  //                        if(fieldAndFunc)
  //                        println("是类 ${fieldAndFunc.text} ${fieldAndFunc.elementType}")
                          itemClass.children.forEach { fieldAndFunc ->
                              fieldAndFunc.children.forEach { fieldDeclaration ->
                                  if (fieldDeclaration is DartVarDeclarationListImpl) {

                                      // 获取字段名称

                                      // 获取字段名称
  //                                    val componentName = fieldDeclaration.componentName
  //                                    val fieldName = fieldDeclaration.name!!

                                      // 获取字段类型

                                      // 获取字段类型
                                      val tokenType = fieldDeclaration.tokenType
                                      val elementType = fieldDeclaration.elementType
  //                                    val dartType: DartType = fieldDeclaration.getDartType()
  //                                    val fieldTypeName = if (dartType != null) dartType.text else "dynamic"
  //
  //                                    // 输出字段类型
                                      fieldDeclaration.children.forEach { itemFieldType ->
                                          println("Field Name: ${itemFieldType.text} ${itemFieldType::class.java.name}")
                                      }
  //                                    // 输出字段类型

  //                                    println("Field Type: $fieldTypeName")
  //                                    println("是类 ${fieldDeclaration.text} ${fieldDeclaration.elementType}")
                                  }
                                  println("是类 ${fieldDeclaration.text} ${fieldDeclaration.elementType} ${fieldDeclaration::class.java.name}")
                              }

                          }
                      }

                  }
              }
  */

            val mutableMapOf = mutableListOf<HelperClassGeneratorInfo>()
            val imports: MutableList<String> = mutableListOf()
            file.children.forEach {
                val text = it.text
                val classNode = it?.node
                //是类
                val isJsonSerializable = text.contains("@JsonSerializable")
                if (classNode?.elementType == DartTokenTypes.CLASS_DEFINITION && isJsonSerializable
                ) {
                    if (classNode is CompositeElement) {
                        val helperClassGeneratorInfo = HelperClassGeneratorInfo()
                        for (filedAndMethodNode in classNode.children()) {
                            val nodeName = filedAndMethodNode.text
                            //是类里字段
                            if (filedAndMethodNode.elementType == DartTokenTypes.CLASS_BODY) {
                                filedAndMethodNode.children().forEach { itemFile ->
                                    //debug看数据用
                                    val itemFileText = itemFile.text
                                    itemFile.children().forEach { itemFileNode ->
                                        //debug看数据用
                                        val itemFileNodeText = itemFileNode.text
                                        //itemFileNode text : int code
                                        if (itemFileNode.elementType == DartTokenTypes.VAR_DECLARATION_LIST) {
                                            var nameNode: String? = null
                                            var typeNodeInfo: FieldClassTypeInfo? = null
                                            var isLate = false
                                            var isStatic = false
                                            //当前字段的所有注解
                                            val allAnnotation = mutableListOf<AnnotationValue>()
                                            itemFileNode.firstChildNode.children().forEach lit@{ fieldWholeNode ->
                                                //如果第一个是注解,解析注解里的内容
                                                val fieldWholeNodeText = fieldWholeNode.text
                                                if (fieldWholeNodeText == "static") {
                                                    //什么也不干
                                                    isStatic = true
                                                } else if (fieldWholeNodeText == "final") {
                                                    //什么也不干
                                                    return@lit
                                                } else if (fieldWholeNodeText.trim().isEmpty()) {
                                                    //什么也不干
                                                    return@lit
                                                } else if (isStatic) {
                                                    return@lit
                                                } else if (fieldWholeNode.elementType == DartTokenTypes.METADATA) {///如果包含注解
                                                    val annotationWholeNode = fieldWholeNode.firstChildNode;
                                                    //@JSONField(name: 'app',serialize:true) 为例
                                                    if (
                                                    //@
                                                        annotationWholeNode.text == "@" &&
                                                        //JSONField
                                                        fieldWholeNode.firstChildNode.treeNext.elementType == DartTokenTypes.REFERENCE_EXPRESSION && fieldWholeNode.firstChildNode.treeNext.text == "JSONField"
                                                    ) {

                                                        if (fieldWholeNode.firstChildNode.treeNext.treeNext.elementType == DartTokenTypes.ARGUMENTS) {
                                                            fieldWholeNode.firstChildNode.treeNext.treeNext.children()
                                                                .forEach { onlyItemWholeMetaValueDataNode ->
                                                                    //onlyItemWholeMetaValueDataNode 只有注解的多个内容:name: 'app',serialize:true
                                                                    if (onlyItemWholeMetaValueDataNode.elementType == DartTokenTypes.ARGUMENT_LIST) {
                                                                        println("注解22 ${onlyItemWholeMetaValueDataNode.text}")

                                                                        onlyItemWholeMetaValueDataNode.children()
                                                                            .forEach { onlyItemMetaValueDataNode ->
                                                                                // onlyItemMetaValueDataNode  只有注解的单个内容:name: 'app'
                                                                                println("注解33 ${onlyItemMetaValueDataNode.text}")
                                                                                if (onlyItemMetaValueDataNode.elementType == DartTokenTypes.NAMED_ARGUMENT) {
                                                                                    var annotationName: String? = null
                                                                                    var annotationValue: Any? = null
                                                                                    onlyItemMetaValueDataNode.children()
                                                                                        .forEach { onlyItemNamedArgumentValueDataNode ->
                                                                                            //注解里内容的名字  name: 'app' 里的name
                                                                                            when (onlyItemNamedArgumentValueDataNode.elementType) {
                                                                                                DartTokenTypes.PARAMETER_NAME_REFERENCE_EXPRESSION -> {
                                                                                                    annotationName =
                                                                                                        onlyItemNamedArgumentValueDataNode.text.replace(
                                                                                                            "\'",
                                                                                                            ""
                                                                                                        ).replace(
                                                                                                            "\"",
                                                                                                            ""
                                                                                                        )
                                                                                                }

                                                                                                DartTokenTypes.STRING_LITERAL_EXPRESSION -> {
                                                                                                    annotationValue =
                                                                                                        onlyItemNamedArgumentValueDataNode.text.replace(
                                                                                                            "\'",
                                                                                                            ""
                                                                                                        ).replace(
                                                                                                            "\"",
                                                                                                            ""
                                                                                                        )
                                                                                                }

                                                                                                DartTokenTypes.LITERAL_EXPRESSION -> {
                                                                                                    annotationValue =
                                                                                                        (onlyItemNamedArgumentValueDataNode.text == "true")
                                                                                                }
                                                                                            }

                                                                                            println("注解的内容 ${onlyItemNamedArgumentValueDataNode.text}")
                                                                                        }
                                                                                    if (annotationName != null && annotationValue != null) {
                                                                                        //注解的实际内容
                                                                                        allAnnotation.add(
                                                                                            AnnotationValue(
                                                                                                annotationName!!,
                                                                                                annotationValue!!
                                                                                            )
                                                                                        )
                                                                                    }
                                                                                }

                                                                            }
                                                                    }

                                                                }
                                                        }
                                                    }
                                                } else {
                                                    val isVar =
                                                        fieldWholeNodeText == "var"
                                                    fieldWholeNode.children().forEach {
                                                        it.children().forEach { it2 ->
                                                            println("普通解析22222 ${it2.text} ${it2::class.java.name}")
                                                        }
                                                        println("普通解析222 ${it.firstChildNode.text} ${it}")
                                                    }
                                                    //不是注解,普通解析
                                                    when {
                                                        fieldWholeNodeText == "late" || fieldWholeNodeText == "=" -> {
                                                            isLate = true
                                                        }

                                                        isVar -> {
                                                            typeNodeInfo =
                                                                FieldClassTypeInfo(
                                                                    primaryType = "dynamic",
                                                                    nullable = true
                                                                )
                                                        }

                                                        fieldWholeNode.elementType == DartTokenTypes.TYPE -> {
                                                            ///说明有泛型
                                                            if (fieldWholeNode.children().toList().isNotEmpty()) {
                                                                typeNodeInfo =
                                                                    FieldClassTypeInfo.parseFieldClassTypeInfo(
                                                                        fieldWholeNode.children().first().children()
                                                                            .toList()
                                                                    )
                                                                println(typeNodeInfo)
                                                                println("普通解析222 $fieldWholeNodeText")
                                                            } else {
                                                                println("没有泛型")
                                                            }

                                                        }

                                                        fieldWholeNode.elementType == DartTokenTypes.COMPONENT_NAME -> {
                                                            ///私有属性不考虑
                                                            if (fieldWholeNodeText.startsWith("_")) {
                                                                return@lit
                                                            } else {
                                                                nameNode = fieldWholeNodeText
                                                            }
                                                        }
                                                        //  println("普通解析类型 ${itemFieldNode.elementType}")
                                                        //  println("普通解析类型文本 ${itemFieldNode.text}")
                                                    }
                                                    if (fieldWholeNode.elementType is DartElementType) {
                                                        if (notSupportType.contains(fieldWholeNodeText)) {
                                                            val errorMessage =
                                                                "This file contains code that cannot be parsed: ${file.name}. content: ${nodeName}. type not supported ,such as ${notSupportType.joinToString()}"
                                                            throw RuntimeException(errorMessage)
                                                        }
                                                    }
                                                    println("普通解析类型文本 $fieldWholeNodeText 普通解析类型 ${fieldWholeNode.elementType}")
                                                }

                                            }
                                            //如果不是late,但是最后一行包括=号,说明默认赋值了
                                            if (!isLate && itemFileNode.lastChildNode.text.contains("=")) {
                                                isLate = true
                                            }
                                            if (nameNode != null && typeNodeInfo != null) {
                                                helperClassGeneratorInfo.addFiled(
                                                    typeNodeInfo!!,
                                                    nameNode!!,
                                                    isLate,
                                                    allAnnotation
                                                )
                                            }
                                        }
//                                    var text4 = itemFileNode.text
//                                    var text5 = itemFileNode.text
                                    }
//                                val text2 = itemFile.text
//                                val text3 = itemFile.text
                                }
                            } else if (filedAndMethodNode.elementType == DartTokenTypes.COMPONENT_NAME) {
                                helperClassGeneratorInfo.className = (nodeName)
                            } /*else if (filedAndMethodNode.elementType == DartTokenTypes.MIXINS) {
                                //不包含JsonConvert 那么就不转
                                if (nodeName.contains("JsonConvert").not()) {
                                    continue
                                }
                            }*/

                        }
                        mutableMapOf.add(helperClassGeneratorInfo)
//                    classNode.children() {filedAndMethodNode->
//                        val text1 = filedAndMethodNode.text
//                    }
                    }
                } else if (classNode?.elementType == DartTokenTypes.IMPORT_STATEMENT) {
                    imports.add(text)
                }

                /* it?.node?.children()?.forEach {
                 val toString = it?.firstChildNode?.toString()
                 val toString33 = it?.lastChildNode?.toString()
            }*/
            }
            if (mutableMapOf.isEmpty()) null else HelperFileGeneratorInfo(imports, mutableMapOf)
        } else null
    }
}