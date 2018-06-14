package com.sososeen09.multidexbuild;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sososeen09.multidexbuild.utils.ReflectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends Activity {
    private TextView tvResult;
    private TextView tvFix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvFix = (TextView) findViewById(R.id.tv_fix);

    }

    public void getResult(View view) {
        tvResult.setText("show the result: " + SimpleMathUtils.divide());
    }

    public void fix(View view) {
        tvFix.setText("fixing...");
        File originDex = null;
        try {
            originDex = copyFileFromAssets("fixed.dex", getCacheDir().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (originDex != null) {
            File dexOptimizeDir = getDir("dex", Context.MODE_PRIVATE);
            String dexOutputPath = dexOptimizeDir.getAbsolutePath();
            PathClassLoader pathClassLoader = (PathClassLoader) getClassLoader();
            DexClassLoader dexClassLoader = new DexClassLoader(originDex.getAbsolutePath(), dexOutputPath, null,
                    pathClassLoader);

            try {
                // 获取DexClassLoader对象的pathList对象，DexPathList
                Object dexPathListWithDexClassLoader = ReflectUtils.findField(dexClassLoader, "pathList").get(dexClassLoader);

                // 获取DexPathList对象Element[]数组，对应的字段名是dexElements
                Field dexElements = ReflectUtils.findField(dexPathListWithDexClassLoader, "dexElements");
                Object[] elements = (Object[]) dexElements.get(dexPathListWithDexClassLoader);

                // 获取PathClassLoader对象的pathList对象，DexPathList
                Object dexPathListWithPathClassLoader = ReflectUtils.findField(pathClassLoader, "pathList").get(pathClassLoader);

                //把之前获取的Element[]数组插入到PathClassLoader对象对应的DexPathList的Element数组中
                ReflectUtils.insertFieldArray(dexPathListWithPathClassLoader, "dexElements", elements);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        tvFix.setText("done!");
    }

    private File copyFileFromAssets(String assetName, String dexOutputDir) throws IOException {
        File originDex = null;
        AssetManager assets = getAssets();
        InputStream open = assets.open(assetName);
        originDex = new File(dexOutputDir, assetName);
        FileOutputStream fileOutputStream = new FileOutputStream(originDex);
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = open.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        fileOutputStream.close();
        open.close();

        return originDex;
    }
}
