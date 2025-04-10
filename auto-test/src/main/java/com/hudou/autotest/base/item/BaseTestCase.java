package com.hudou.autotest.base.item;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.fos;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultData;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.graphics.Color;

//import com.hudou.autotest.MainActivity;
import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.report.excel.ExcelUtils;
import com.hudou.autotest.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseTestCase {
    public static volatile boolean isCompleted = false;
    public static volatile boolean isPaused = false;

    public void runAllCases(Class<? extends BaseTestCase> clz){
        runTestCases(clz, Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .toArray(Method[]::new));
    }

    public void runCase(Class<? extends BaseTestCase> clz, int id){
        Method[] testCaseMethods = Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .toArray(Method[]::new);
        if (id >= testCaseMethods.length || id < 0){
            return;
        }
        Method[] runMethod = new Method[] {testCaseMethods[id]};
        runTestCases(clz, runMethod);
    }

    public void runPartContinueCases(Class<? extends BaseTestCase> clz, int beginId, int endId){
        runTestCases(clz, Arrays.copyOfRange(Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .toArray(Method[]::new), beginId, endId + 1));
    }


    private void runTestCases(Class<? extends BaseTestCase> clz, Method[] testCaseMethods) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(testCaseMethods.length);
        isCompleted = false;
        isPaused = false;

        for (Method method : testCaseMethods) {
            executor.submit(() -> {
                try {
                    if (isPaused) {
                        latch.countDown(); // 直接减少倒计时，跳过当前任务
                        return;
                    }
                    resultData = new ResultData();
                    if ((resultItemList != null) && resultItemList.isEmpty()){
                        resultItemList.add(new ResultItem(clz, new ArrayList<>()));
                    }
                    boolean exist = false;
                    for (ResultItem resultItem : resultItemList) {
                        if (resultItem.getClz().equals(clz)) {
                            resultItem.setStartTime(ExcelUtils.testCaseDate(String.valueOf(new Date().getTime())));
                            exist = true;
                            break;
                        }
                    }
                    if (!exist){
                        resultItemList.add(new ResultItem(clz, new ArrayList<>()));
                    }
                    resultData.setId(method.getName());
                    resultData.setTestCaseName(ReflectionUtils.getAnnotationValue(method, TestCase.class, "name"));
                    resultData.setDetail("XX=============" + method.getName() + "=============XX");

                    postValue(Color.BLUE, "开始执行案例：" + ReflectionUtils.getAnnotationValue(method, TestCase.class, "name") + "\n");
                    // 执行前置方法
                    onCaseStart(method);

                    // 执行测试方法
                    method.setAccessible(true); // 允许访问私有方法
                    method.invoke(this); // 调用方法

                    // 执行后置方法
                    onCaseFinish(method);
                    postValue(Color.BLUE, "案例：" + ReflectionUtils.getAnnotationValue(method, TestCase.class, "name") + "，执行结束");


                    //Add storage start
                    List<ResultData> clzResultDataList = getResultDataListForClass(clz);
                    if (clzResultDataList == null){
                        clzResultDataList = new ArrayList<>();
                    }
                    for (ResultItem resultItem : resultItemList) {
                        if (resultItem.getClz().equals(clz)) {
                            boolean isExist = false;
                            for (int i = 0; i < clzResultDataList.size(); i++) {
                                if (clzResultDataList.get(i).getTestCaseName().equals(resultData.getTestCaseName())){
                                    isExist = true;
                                }
                            }
                            if (!isExist){
                                resultItem.getResultDataList().add(resultData);
                            }
                            resultItem.setEndTime(ExcelUtils.testCaseDate(String.valueOf(new Date().getTime())));

                            break;
                        }
                    }

                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown(); // 每完成一个任务，倒计时减一
                }
            });
        }

        executor.shutdown(); // 关闭ExecutorService

        // 在主线程中等待所有任务完成
        new Thread(() -> {
            try {
                latch.await(); // 等待所有任务完成
                isCompleted = true;
                postValue(Color.YELLOW, "案例执行完毕，可点击返回按钮继续\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String viewCaseDetails(Class<? extends BaseTestCase> clz){
        StringBuilder details = new StringBuilder();
        Method[] declaredMethods = clz.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (method.isAnnotationPresent(TestCase.class)) {
                TestCase testCaseAnnotation = method.getAnnotation(TestCase.class);
                String name = testCaseAnnotation != null ? testCaseAnnotation.name() : null;
                details = details.append(i).append(" : ").append(name).append("\n");
            }
        }
        return details.toString();
    }

    public int testItemCasesNum(Class<? extends BaseTestCase> clz){
        return Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .toArray(Method[]::new).length;
    }


    protected void postValue(int color, String message){
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AutoTestMainActivity.mShowMessage.postValue(new ShowMessage(color, message));
        resultData.appendDetail("\n" + message);
        try {
            fos.write((message + "\n").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ResultData> getResultDataListForClass(Class<? extends BaseTestCase> clz) {
        for (ResultItem resultItem : resultItemList) {
            if (resultItem.getClz().equals(clz)) {
                return resultItem.getResultDataList();
            }
        }
        return null;
    }



    public abstract void onCaseStart(Method method);
    public abstract void onCaseFinish(Method method);

}
