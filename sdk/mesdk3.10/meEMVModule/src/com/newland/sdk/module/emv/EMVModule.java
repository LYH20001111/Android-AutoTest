package com.newland.sdk.module.emv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.mtype.Module;
import com.newland.sdk.utils.TLVPackage;

import java.util.List;


/**
 * @description: Emv module interface
 * @author: Lindan
 * @create: 2019/7/29
 * @since 3.10.01
 */
public interface EMVModule extends Module {
    /**
     * <p>Initialize the emv module.</p>
     * <p><strong>this method should be called before any other emv mothod.</strong></p>
     * @param context
     * @param emvExtParams use internal EMV or external pinpad EMV.
     * @since 3.10.01
     */
    public boolean init(Context context,EmvExtParams emvExtParams);


    /**
     * <p>Add an CAPK to the terminal. </p>
     *
     * @param inputData <p>The CAPK with BER-TLV format.</p>
     *                  <p>there are two way to obtain the tlv data.</p>
     *                  <p>1.complete an instance of {@link CAPK} and then obtain the tlv data from EMVUtils.newEmvPackager().pack(CAPK)</p>
     *                  <p>2.get an instance of EMVUtils.newTlvPackage() and then invoke the append() method to fill data and invoke the pack() method to obtain the tlv data.</p>
     * @return
     */
    public boolean addCAPublicKey(@NonNull byte[] inputData);

    /**
     * Delete the public key for the specified rid
     *
     * @param rid   RID(Registered Application Provider Identifier)
     * @param index public key index. If the index value is null, all public keys contained in the RID are deleted
     * @return boolean
     * @since 3.10.01
     */
    public boolean deleteCAPublicKey(byte[] rid, @Nullable Integer index);

    /**
     * Delete all public keys in the terminal.
     *
     * @return boolean
     * @since 3.10.01
     */
    public boolean deleteAllCAPublicKey();

    /**
     * Get the public key for the specified rid.
     *
     * @param rid   Identity authentication center (application provider)（0x9F06）
     * @param index Corresponding public key index（0x9F22）
     * @return Capk data{@link CAPK}
     * @since 3.10.01
     */
    public CAPK getSpecifiedCAPublicKey(byte[] rid, @NonNull int index);

    /**
     * Get all public keys of the terminal.
     *
     * @return All capk data{@link CAPK}
     * @since 3.10.01
     */
    public List<CAPK> getAllCAPublicKey();

    /**
     * <p>Add an AID to the terminal. </p>
     *
     * @param inputData      <p>The AID with BER-TLV format.</p>
     *                       <p>there are two way to obtain the tlv data.</p>
     *                       <p>1.complete an instance of {@link AID} and then obtain the tlv data from EMVUtils.newEmvPackager().pack(AID)</p>
     *                       <p>2.get an instance of EMVUtils.newTlvPackage() and then invoke the append() method to fill data and invoke the pack() method to obtain the tlv data.</p>
     * @param aidStorageMode Used for contact or contactless transaction.
     * @return
     */
    public boolean addAID(@NonNull byte[] inputData, CardInterface aidStorageMode);

    /**
     * <p>load terminal transaction configuration from the xml.</p>
     * <p>file path:assets</p>
     *
     * @param fileName the name of the default transaction configuaration file.
     * @return
     */
    public boolean loadConfigurationFromXML(String fileName);

    /**
     * Delete the AID.
     *
     * @param aid            <p>Application Identifier(0x9f06).</p>
     *                       <p>if null for this param,all aids are removed.</p>
     * @param aidStorageMode Used for contact or contactless transaction.
     * @return boolean
     * @since 3.10.01
     */
    public boolean deleteAID(byte[] aid, CardInterface aidStorageMode);

    /**
     * Get all AID in the terminal.
     * @param aidStorageMode Used for contact or contactless transaction.
     * @return AID data list{@link AID}
     * @since 3.10.01
     */
    /**
     * @param aid <p>Application Identifier(0x9f06).</p>
     *            <p>if null for this param,fetch all aids.</p>
     * @return
     */
    public List<AID> getAID(byte[] aid, CardInterface aidStorageMode);

    /**
     * <p>Set the terminal configuration. </p>
     *
     * @param tlvData        <p>The terminal configuration with BER-TLV format.</p>
     *                       <p>there are two way to obtain the tlv data.</p>
     *                       <p>1.complete an instance of {@link TerminalConfiguration} and then obtain the tlv data from EMVUtils.newEmvPackager().pack(TerminalConfiguration)</p>
     *                       <p>2.get an instance of EMVUtils.newTlvPackage() and then invoke the append() method to fill data and invoke the pack() method to obtain the tlv data.</p>
     * @param aidStorageMode Used for contact or contactless transaction.
     * @return
     */
    public boolean setTerminalConfiguration(byte[] tlvData, CardInterface aidStorageMode);

    /**
     * <p>Get an EMV transaction controller</p>
     * Each transaction should keep the same EMVTransController instance.</p>
     *
     * @param emvControllerListener EMV listener can be customized by user, according to the
     *                              definition for the response processing in this transaction
     * @return EMV Controller
     * @since 3.10.01
     */
    public EMVTransController getEmvTransController(EMVControllerListener emvControllerListener);

//    /**
//     * <p>Get an EMV transaction controller for a external Card Reader.</p>
//     * Each transaction should keep the same EMVTransController instance.</p>
//     *
//     * @param emvControllerListener EMV callback listener
//     * @return EMV controller
//     */
//    public EMVTransController getEmvExtController(EMVControllerListener emvControllerListener);

    /**
     * Get card information and EC balance.
     *
     * @return EMVCardInfo  Card information.{@link EMVCardInfo}
     */
    public EMVCardInfo getCardInformation();

    /**
     * Get the EMV transaction logs
     *
     * @param transLogListener EMV transaction log callback listener
     * @since 3.10.01
     */
    public void getEMVTransLogs(EMVTransLogListener transLogListener);

    /**
     * Get the EC transaction logs <p>
     *
     * @param transLogListener EC transaction log callback listener
     * @since 3.10.01
     */
    public void getECTransLogs(ECTransLogListener transLogListener);

    /**
     * Set the data to the emv kernel.
     *
     * @param tag   Tag
     * @param value Value
     * @return
     */
    public boolean setEmvData(int tag, byte[] value);

    /**
     * Get the emv kernel data.
     *
     * @param emvTags Array of tags
     * @return {@link TLVPackage}
     */
    public TLVPackage getEmvData(int[] emvTags);

    /**
     * Get the emv kernel data.
     *
     * @param tag emv tag
     * @return emv data
     */
    byte[] getEmvData(int tag);

    /**
     * Use the GET DATA Command to get the value of the Tag.
     *
     * @param tag Tag
     * @return
     */
    public byte[] getICCdata(int tag);

    /**
     * get EMV Kernel version
     * @return
     */
    public String getEMVKernelVersion();

    /**
     * set status indicators and sucess/alert tone while contactless emv process
     * @param isEnable true: supported; false: unsupported
     */
    public void setIndicatorsAndBeep(boolean isEnable);
}
