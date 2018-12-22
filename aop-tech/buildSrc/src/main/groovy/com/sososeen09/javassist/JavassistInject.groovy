package com.sososeen09.javassist

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.CtNewMethod
import org.gradle.api.Project


class JavassistInject {

    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"Hello,Javassist\" ); ";

    static void injectDir(String path, String packageName, Project project) {
        def log = project.logger
        pool.appendClassPath(path)
        //project.android.bootClasspath 加入android.jar，否则找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {
                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end)
                                .replace('\\', '.').replace('/', '.')

                        //开始修改class文件
                        CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) {
                            c.defrost()
                        }


                        if (c.getName().endsWith("Activity") || c.getSuperclass().getName().endsWith("Activity")) {
//                        if (Class.forName(c.getName()).isAssignableFrom(Class.forName("android.app.Activity"))) {
                            log.error("modify Class: " + className)
                            CtConstructor[] cts = c.getDeclaredConstructors()
                            if (cts == null || cts.length == 0) {
                                //手动创建一个构造函数
                                CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                                constructor.insertBeforeBody(injectStr)
                                c.addConstructor(constructor)
                            } else {
                                cts[0].insertBeforeBody(injectStr)
                            }

                            //重写finish方法

                            CtMethod closeKeyboardMethod = CtMethod.make(closeKeyboard_strinbg, c)
                            c.addMethod(closeKeyboardMethod)
                            CtMethod finishMethod = CtNewMethod.make(Pre_Finish, c)
                            c.addMethod(finishMethod)
                            log.error("finishMethod: ")

                        }
                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
        }
    }

    static def Pre_Finish = "  \n" +
            "    public void finish() {\n" +
            "           closeKeyboard();\n"+
            "        super.finish();\n"+
            "}"

    static def closeKeyboard_strinbg='''
        private void closeKeyboard() {
        try {
            android.view.View view = getWindow().peekDecorView();
            if (view != null) {
                android.view.inputmethod.InputMethodManager inputMethodManager = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            //ignore
        }
       }
    '''
}
