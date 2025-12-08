package com.hudou.autotest.base.item;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.fos;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultData;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.graphics.Color;

import androidx.annotation.RestrictTo;

import com.hudou.autotest.annotation.TestCase;
import com.hudou.autotest.base.activity.AutoTestMainActivity;
import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.constant.ShowMessage;
import com.hudou.autotest.database.entity.ResultDataEntity;
import com.hudou.autotest.database.entity.ResultItemEntity;
import com.hudou.autotest.report.excel.ExcelUtils;
import com.hudou.autotest.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class BaseTestCase {
    public static volatile boolean isCompleted = false;
    public static volatile boolean isPaused = false;

    public void runAllCases(Class<? extends BaseTestCase> clz) {
        runTestCases(clz, Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new));
    }

    public void runCase(Class<? extends BaseTestCase> clz, int id) {
        Method[] testCaseMethods = Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new);
        if (id >= testCaseMethods.length || id < 0) {
            return;
        }
        Method[] runMethod = new Method[]{testCaseMethods[id]};
        runTestCases(clz, runMethod);
    }

    public void runPartContinueCases(Class<? extends BaseTestCase> clz, int beginId, int endId) {
        runTestCases(clz, Arrays.copyOfRange(Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new), beginId, endId + 1));
    }


    private void runTestCases(Class<? extends BaseTestCase> clz, Method[] testCaseMethods) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(testCaseMethods.length);
        isCompleted = false;
        isPaused = false;

        for (Method method : testCaseMethods) {
            executor.execute(() -> {
                try {
                    if (isPaused) {
                        latch.countDown(); // 直接减少倒计时，跳过当前任务
                        return;
                    }
                    resultData = new ResultData();
                    if ((resultItemList != null) && resultItemList.isEmpty()) {
                        ResultItem item = new ResultItem(clz, new CopyOnWriteArrayList<>());
                        item.setStartTime(ExcelUtils.testCaseDate(String.valueOf(new Date().getTime())));
                        resultItemList.add(item);
                    }
                    boolean exist = false;
                    for (ResultItem resultItem : resultItemList) {
                        if (resultItem.getClz().equals(clz)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        ResultItem item = new ResultItem(clz, new CopyOnWriteArrayList<>());
                        item.setStartTime(ExcelUtils.testCaseDate(String.valueOf(new Date().getTime())));
                        resultItemList.add(item);
                    }
                    resultData.setId(method.getName());
                    resultData.setTestCaseName(ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.name));
                    resultData.setEnglishDescription(ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.enDes));
                    resultData.setDetail("XX=============" + method.getName() + "=============XX");

                    postValue(Color.BLUE, "开始执行案例：" + ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.name) + "\n");
                    if (!"".equals(ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.tip))) {
                        postValue(Color.GRAY, "\n" + "案例提示：" + ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.tip));
                    }
                    try {
                        TestCase testCaseAnnotation = method.getAnnotation(TestCase.class);
                        boolean isAbandon = testCaseAnnotation != null && testCaseAnnotation.abandon();
                        if (!isAbandon) {
                            // 执行前置方法
                            onCaseStart(method);

                            // 执行测试方法
                            method.setAccessible(true); // 允许访问私有方法
                            method.invoke(this); // 调用方法

                            // 执行后置方法
                            onCaseFinish(method);

                            // 等待测试结果
                            try {
                                waitForResult(method);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            resultData.setResult("测试通过");
                            postValue(Color.MAGENTA, "\n！ 废弃案例 ！\n");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        resultData.setResult("测试失败");
                        // 解开反射包装，拿到根因
                        Throwable root = e.getCause() != null ? e.getCause() : e;
                        postValue(Color.RED, "测试失败" + ("\n"
                                +  "测试案例抛出异常未捕获，测试框架捕获：" + root.getClass().getSimpleName() + "：" + root.getMessage()));
                    } finally {
                        postValue(Color.BLUE, "案例：" + ReflectionUtils.getAnnotationValue(method, TestCase.class, TestCase.Members.name) + "，执行结束");
                    }

                    //Add storage start
                    CopyOnWriteArrayList<ResultData> clzResultDataList = getResultDataListForClass(clz);
                    if (clzResultDataList == null) {
                        clzResultDataList = new CopyOnWriteArrayList<>();
                    }
                    for (ResultItem resultItem : resultItemList) {
                        if (resultItem.getClz().equals(clz)) {
                            boolean isExist = false;
                            int index = -1;
                            for (int i = 0; i < clzResultDataList.size(); i++) {
                                if (clzResultDataList.get(i).getId().equals(resultData.getId())) {
                                    isExist = true;
                                    index = i;
                                    break;
                                }
                            }
                            if (isExist) {
                                resultItem.getResultDataList().remove(index);// 删除当前匹配的元素
                                resultItem.getResultDataList().add(index, resultData);// 添加新的 resultData
                            } else {
                                resultItem.getResultDataList().add(resultData);
                            }
                            resultItem.setEndTime(ExcelUtils.testCaseDate(String.valueOf(new Date().getTime())));

                            break;
                        }
                    }

                    onCaseEnd(method);//这里是案例最终结束

                    new Thread(() -> {
                        for (ResultItem ri : resultItemList) {
                            // Upsert ResultItem
                            ResultItemEntity itemE = new ResultItemEntity();
                            itemE.className = ri.getClz().getName();
                            itemE.startTime = ri.getStartTime();
                            itemE.endTime = ri.getEndTime();
                            itemE.isStartTimeSet = ri.isStartTimeSet();
                            AutoTestMainActivity.getDb().dao().upsertResultItem(itemE);

                            // Upsert ResultData
                            CopyOnWriteArrayList<ResultDataEntity> dataEs = new CopyOnWriteArrayList<>();
                            for (ResultData rd : ri.getResultDataList()) {
                                ResultDataEntity de = new ResultDataEntity();
                                de.className = ri.getClz().getName();
                                de.caseName = rd.getId();
                                de.methodName = rd.getTestCaseName();
                                de.result = rd.getResult();
                                de.chineseDescription = rd.getChineseDescription();
                                de.englishDescription = rd.getEnglishDescription();
                                de.detail = rd.getDetail();
                                dataEs.add(de);
                            }
                            AutoTestMainActivity.getDb().dao().upsertResultDataList(dataEs);
                        }
                    }).start();


                } catch (Exception e) {
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

    public String viewCaseDetails(Class<? extends BaseTestCase> clz) {
        // 2. 只有第一次会走 lambda，后续直接拿结果
        return AutoTestMainActivity.getCaseDetailsMap().computeIfAbsent(clz, this::buildCaseDetails);
    }

    /* 3. 真正的构建逻辑，抽出去复用 */
    private String buildCaseDetails(Class<? extends BaseTestCase> clz) {
        Method[] methods = Arrays.stream(clz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))
                .toArray(Method[]::new);

        StringBuilder sb = new StringBuilder(methods.length * 32);
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getAnnotation(TestCase.class).name();
            sb.append(i).append(" : ").append(name).append('\n');
        }
        return sb.toString();
    }

    public String viewAbandonCaseDetails(Class<? extends BaseTestCase> clz) {
        StringBuilder details = new StringBuilder();
        Method[] testCaseMethods = Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new);

        for (int i = 0; i < testCaseMethods.length; i++) {
            Method method = testCaseMethods[i];
            if (method.isAnnotationPresent(TestCase.class)) {
                TestCase testCaseAnnotation = method.getAnnotation(TestCase.class);
                boolean isAbandon = testCaseAnnotation != null && testCaseAnnotation.abandon();
                if (isAbandon) {
                    details = details.append(i).append(" : ").append(testCaseAnnotation.name()).append("\n");
                }
            }
        }
        return details.toString();
    }

    public int testItemCasesNum(Class<? extends BaseTestCase> clz) {
        return Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new).length;
    }

    public int testItemAbandonCasesNum(Class<? extends BaseTestCase> clz) {
        Method[] methods = Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(TestCase.class))
                .sorted(Comparator.comparing(Method::getName))//设置为只根据方法名进行排序，无视方法关键字
                .toArray(Method[]::new);
        return (int) Arrays.stream(methods)
                .filter(method -> method.getAnnotation(TestCase.class).abandon())
                .count();
    }


    protected void postValue(int color, String message) {
        resultData.appendDetail("\n" + message);
        try {
            AutoTestMainActivity.getRecorder().synchronizedPostValue(new ShowMessage(color, message));
            fos.write((message + "\n").getBytes());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void postValue(boolean isDebug, int color, String message) {
        if (isDebug) {
            postValue(color, message);
        } else {
            resultData.appendDetail("\n" + message);
            try {
                fos.write((message + "\n").getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private CopyOnWriteArrayList<ResultData> getResultDataListForClass(Class<? extends BaseTestCase> clz) {
        for (ResultItem resultItem : resultItemList) {
            if (resultItem.getClz().equals(clz)) {
                return resultItem.getResultDataList();
            }
        }
        return null;
    }

    public abstract void onItemStart();

    public abstract void onCaseStart(Method method);

    public abstract void onCaseFinish(Method method);

    public abstract void onCaseEnd(Method method);

    // 等待测试结果的抽象方法
    protected abstract void waitForResult(Method method) throws InterruptedException;

}
