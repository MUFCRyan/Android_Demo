package com.mufcryan.apm_aspectj.aop

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.mufcryan.apm_aspectj.core.job.activity.AH
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import java.lang.Exception

/** 监控 App 冷热启动耗时及 Lifecycle 耗时 */
@Aspect
class TraceActivity {
    companion object{
        private const val BASE_PACKAGE_NAME = "com.mufcryan.apm_aspectj"
    }

    // 1. 定义切入点方法 baseCondition --> 排除 ArgusAPM 中相应的类
    @Pointcut("!within($BASE_PACKAGE_NAME.aop.*) && !within($BASE_PACKAGE_NAME.core.job.activity.*)")
    fun baseCondition(){
    }

    // 2. 定义切入点 applicationOnCreate --> 执行 Application onCreate()
    @Pointcut("execution(* android.app.Application.onCreate(android.content.Context)) && args(context)")
    fun applicationOnCreate(context: Context){

    }

    // 3. 定义后置通知 applicationOnCreateAdvice --> 在 application 的 onCreate() 执行完之后插入 AH.applicationOnCreate(context) 这行代码
    @After("applicationOnCreate(context)")
    fun applicationOnCreateAdvice(context: Context){
        AH.applicationOnCreate(context)
    }

    // 4. 定义切入点 --> 执行 Application 的 attachBaseContext()
    @Pointcut("execution(* android.app.Application.attachBaseContext(android.content.Context)) && args(context)")
    fun applicationAttachBaseContext(context: Context){

    }

    // 5. 定义前置通知 --> 在 Application 的 attachBaseContext() 执行之前插入 AH.applicationAttachBaseContext()
    @Before("applicationAttachBaseContext(context)")
    fun applicationAttachBaseContextAdvice(context: Context){
        AH.applicationAttachBaseContext(context)
    }

    // 6. 定义切入点 --> 执行所有 Activity 中的 onXXX()，同时用 baseCondition() 排除当前目录
    @Pointcut("execution(* android.app.Activity.on**(..)) && baseCondition()")
    fun activityOnXXX(){

    }

    // 7. 定义环绕通知 --> 在所有 Activity 的 onXXX() 前后插入相应代码
    @Around("activityOnXXX()")
    fun activityOnXXXAdvice(proceedingJoinPoint: ProceedingJoinPoint): Any?{
        var result: Any? = null
        try {
            val activity = proceedingJoinPoint.target as Activity
            val startTime = System.currentTimeMillis()
            result = proceedingJoinPoint.proceed()
            val activityName = activity.javaClass.canonicalName

            val signature = proceedingJoinPoint.signature
            var sign = ""
            var methodName = ""
            if(signature != null){
                sign = signature.toLongString()
                methodName = signature.name
            }

            if(!TextUtils.isEmpty(activityName) && !TextUtils.isEmpty(sign) && sign.contains(activityName)){
                invoke(activity, startTime, methodName, sign)
            }
        } catch (e: Exception){

        }
        return result
    }

    fun invoke(activity: Activity, startTime: Long, methodName: String, sign: String){
        AH.invoke(activity, startTime, methodName, sign)
    }
}