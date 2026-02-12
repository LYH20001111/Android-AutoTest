package com.newland.sdk.me.module.emv;

import java.util.ArrayList;
import java.util.List;

/**
 * emv 执行步骤<p>
 */
public class EMVTransStep {

    /**
     * emv交易未开启<p>
     */
    static EMVTransStep PREPARED = new EMVTransStep(0);
    /**
     * 应用选择,该步骤声明等同于{@link AbstractEMVTransController#_EMV_PROC_TO_APPSEL_INIT}<p>
     */
    static EMVTransStep APPLICATION_SELECT = new EMVTransStep(1);
    /**
     * 交易信息获取,该步骤声明等同于{@link AbstractEMVTransController#_EMV_PROC_TO_READAPPDATA}<p>
     */
    static EMVTransStep TRANSINFO_READ = new EMVTransStep(2);
    /**
     * 执行密码输入步骤，该步骤声明等同于<tt>CVM</tt>,持卡人验证<p>
     * 参考{@link AbstractEMVTransController#_EMV_PROC_TO_CV}
     */
    static EMVTransStep PINENTRY_INPUT = new EMVTransStep(3);
    /**
     * 执行交易（若联机则触发联机事件)，该步骤等同于第一次密文生成<p>
     * 参考{@link AbstractEMVTransController#_EMV_PROC_TO_1GENAC}
     */
    static EMVTransStep WAITING_TRANSFER_FINISHED = new EMVTransStep(4);

    /**
     * 联机请求发送中，等待联机请求返回<p>
     */
    static EMVTransStep ONLINEREQUEST = new EMVTransStep(5);
    /**
     * 二次授权<p>该步骤声明等同于{@link AbstractEMVTransController#_EMV_PROC_TO_2GENAC}
     */
    static EMVTransStep SECONDISSUANCE = new EMVTransStep(6);
    /**
     * 交易结束<p>
     */
    static EMVTransStep FINISHED = new EMVTransStep(7);
    /**
     * 用于单独执行某一种交易，例如：获取日志<p>
     * 该步骤声明等同于{@link AbstractEMVTransController#_EMV_PROC_CONTINUE}
     */
    static EMVTransStep CONTINUE = new EMVTransStep(8);


    private int ordinal;

    EMVTransStep(int ordinal) {
        this.ordinal = ordinal;
    }


    /**
     * 根据指定的步骤
     *
     * @param steps
     * @return
     */
    public EMVTransStep next(List<EMVTransStep> steps) {
        EMVTransStep next = null;
        int expectedIndex = 0;
        for (EMVTransStep expected : steps) {
            expectedIndex++;
            if (expected.ordinal == ordinal) {
                break;
            }
        }
        if (expectedIndex < steps.size())
            next = steps.get(expectedIndex);

        return next;
    }

    public int hashCode() {
        return ordinal;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof EMVTransStep))
            return false;

        EMVTransStep step = (EMVTransStep) obj;

        return step.ordinal == this.ordinal;
    }

    /**
     * 插卡默认的步骤流程<p>
     *
     * @return
     */
    public static List<EMVTransStep> defaultTransSteps() {
        List<EMVTransStep> defaultSteps = new ArrayList<EMVTransStep>();
        defaultSteps.add(PREPARED);
        defaultSteps.add(APPLICATION_SELECT);
        defaultSteps.add(TRANSINFO_READ);
        defaultSteps.add(WAITING_TRANSFER_FINISHED);
        return defaultSteps;
    }

    /**
     * 非接默认的步骤流程
     *
     * @return
     */
    public static List<EMVTransStep> transWithoutConfirmSteps() {
        List<EMVTransStep> defaultSteps = new ArrayList<EMVTransStep>();
        defaultSteps.add(PREPARED);
        defaultSteps.add(APPLICATION_SELECT);
        defaultSteps.add(WAITING_TRANSFER_FINISHED);
        return defaultSteps;
    }

    /**
     * 默认标准的查询流程定义<p>
     *
     * @return
     */
    public static List<EMVTransStep> defaultQuerySteps() {
        List<EMVTransStep> defaultSteps = new ArrayList<EMVTransStep>();
        defaultSteps.add(PREPARED);
        defaultSteps.add(TRANSINFO_READ);

        return defaultSteps;
    }
//	/**
//	 * 非接脱机简易流程<p>国外卡0步骤简易流程无法获取卡片数据，切换为统一走到1步骤，并确保强制联机和金额为0
//	 * 
//	 * @return
//	 */
//	public static List<EMVTransStep> defaultRFsimpleSteps(){
//		List<EMVTransStep> defaultSteps = new ArrayList<EMVTransStep>();
//		defaultSteps.add(PREPARED);
//		defaultSteps.add(APPLICATION_SELECT);
//		
//		return defaultSteps;
//	}

//    /**
//     * 默认标准的执行流程(非交易流程）定义，例如：查询卡片交易纪录。<p>
//     *
//     * @return
//     */
//    public static List<EMVTransStep> defaultExecuteSteps() {
//        List<EMVTransStep> defaultSteps = new ArrayList<EMVTransStep>();
//        defaultSteps.add(PREPARED);
//        defaultSteps.add(CONTINUE);
//
//        return defaultSteps;
//    }

}
