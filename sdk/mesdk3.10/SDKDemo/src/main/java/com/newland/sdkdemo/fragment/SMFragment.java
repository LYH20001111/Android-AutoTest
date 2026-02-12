package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.newland.sdk.module.sm.RSAKeyPair;
import com.newland.sdk.module.sm.Sm2Key;
import com.newland.sdk.module.sm.SmModule;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class SMFragment extends BaseFragment {
    private SmModule smModule;
    private RSAKeyPair rsaKeyPair;
    private Sm2Key sm2KeyPair;
    private byte[] sm2EncryData = null;
    private byte[] digestData = null;
    private byte[] signedData = null;
    private static final int INDEX_SM_SHA1 = 1;
    private static final int INDEX_SM_SHA256 = 2;
    private static final int INDEX_SM_SHA512 = 3;
    private static final int INDEX_SM_GENERATE_RSA = 4;
    private static final int INDEX_SM_RSA_ENCRY_DECRY = 5;
    private static final int INDEX_SM_RSA_VERIFY = 6;
    private static final int INDEX_SM_GENERATE_SM2 = 7;
    private static final int INDEX_SM_SM2_ENCY = 8;
    private static final int INDEX_SM_SM2_DECRY = 9;
    private static final int INDEX_SM_GENERATE_SM2_SIGNDIGEST = 10;
    private static final int INDEX_SM_SM2_SIGN = 11;
    private static final int INDEX_SM_SM2_VERIFY = 12;
    private byte[] dataToDecry;

    public SMFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_sm_f);
    }

    @Override
    public void initData() {
        smModule = moduleManage.getSmModule();
    }

    @Override
    public Object getModule() {
        return SMFragment.this;
    }


    @MethodGridEntity(btnnameid = R.string.tv_sha1,functionid = INDEX_SM_SHA1)
    private void calSHA1(){
        try {
            showMessage(context.getString(R.string.cal_sha1), MessageTag.TIP);
            byte[] data = smModule.calcSHA1("123456".getBytes());
            showMessage(context.getString(R.string.cal_sha1_rslt)+(data==null?null:ISOUtils.hexString(data)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.cal_sha1_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_sha256,functionid = INDEX_SM_SHA256)
    private void calSHA256(){
        try {
            showMessage(context.getString(R.string.cal_sha256), MessageTag.TIP);
            byte[] data = smModule.calcSHA256("123456".getBytes());
            showMessage(context.getString(R.string.cal_sha256_rslt)+(data==null?null:ISOUtils.hexString(data)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.cal_sha256_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_sha512,functionid = INDEX_SM_SHA512)
    private void calSHA512(){
        try {
            showMessage(context.getString(R.string.cal_sha512), MessageTag.TIP);
            byte[] data = smModule.calcSHA512("123456".getBytes());
            showMessage(context.getString(R.string.cal_sha512_rslt)+(data==null?null:ISOUtils.hexString(data)), MessageTag.DATA);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.cal_sha512_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.generate_rsa_key_pair,functionid = INDEX_SM_GENERATE_RSA)
    private void generateRSAKey(){
        try {
            showMessage(context.getString(R.string.gen_rsa_key), MessageTag.TIP);;
            rsaKeyPair = smModule.genRSAKeyPair(512, 0x10001);
            if(rsaKeyPair!=null){
                showMessage(context.getString(R.string.rsa_puk_mod)+(rsaKeyPair.pubkey.modulus==null?null:ISOUtils.hexString(rsaKeyPair.pubkey.modulus)), MessageTag.DATA);
                showMessage(context.getString(R.string.pubk_mod_len)+rsaKeyPair.pubkey.modulus.length,MessageTag.DATA);
                showMessage(context.getString(R.string.pubk_exponet)+(rsaKeyPair.pubkey.exponent==null?null:ISOUtils.hexString(rsaKeyPair.pubkey.exponent)), MessageTag.DATA);
                showMessage(context.getString(R.string.prik_mod)+(rsaKeyPair.prikey.modulus==null?null:ISOUtils.hexString(rsaKeyPair.prikey.modulus)), MessageTag.DATA);
                showMessage(context.getString(R.string.prik_mod_len)+rsaKeyPair.prikey.modulus.length,MessageTag.DATA);
                showMessage(context.getString(R.string.prik_mod_exponent)+(rsaKeyPair.prikey.exponent==null?null:ISOUtils.hexString(rsaKeyPair.prikey.exponent)), MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.gen_rsa_null), MessageTag.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.gen_rsa_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.rsa_key_pair_encrypt_and_decrypt,functionid = INDEX_SM_RSA_ENCRY_DECRY)
    private void rsaEncryDecry(){
        try {
            if(rsaKeyPair!=null){
                showMessage(context.getString(R.string.rsa_encry), MessageTag.TIP);
                byte[] encryDataSource = new byte[rsaKeyPair.prikey.modulus.length/8+1];
                byte firstData = (byte) (rsaKeyPair.prikey.modulus[0]-1);
                System.arraycopy(new byte[]{firstData}, 0, encryDataSource, 0, 1);

                for(int i=0;i<rsaKeyPair.prikey.modulus.length/8;i++){
                    System.arraycopy(new byte[]{0x01}, 0, encryDataSource, i+1, 1);
                }
                showMessage(context.getString(R.string.data_to_rsa_enc)+(encryDataSource==null?null:ISOUtils.hexString(encryDataSource)), MessageTag.DATA);
                System.out.println(context.getString(R.string.prik_mod)+ISOUtils.hexString(rsaKeyPair.prikey.modulus));
                System.out.println(context.getString(R.string.prik_mod_len)+rsaKeyPair.prikey.modulus.length);
                System.out.println(context.getString(R.string.prik_mod_exponent)+ISOUtils.hexString(rsaKeyPair.prikey.exponent));
                System.out.println(context.getString(R.string.data_to_rsa_enc)+(encryDataSource==null?null:ISOUtils.hexString(encryDataSource)));

                byte[] rsaEncryData = smModule.rsaRecover(new String(rsaKeyPair.prikey.modulus,"gbk"), rsaKeyPair.prikey.modulus.length/8, rsaKeyPair.prikey.exponent, encryDataSource);
                showMessage(context.getString(R.string.rsa_encry_rslt)+(rsaEncryData==null?null:ISOUtils.hexString(rsaEncryData)), MessageTag.DATA);


                byte[] rsaDecryData = smModule.rsaRecover(new String(rsaKeyPair.pubkey.modulus,"gbk"), rsaKeyPair.pubkey.modulus.length/8, rsaKeyPair.pubkey.exponent, rsaEncryData);
                showMessage(context.getString(R.string.rsa_decry_rslt)+(rsaDecryData==null?null:ISOUtils.hexString(rsaDecryData)), MessageTag.DATA);

            }else{
                showMessage(context.getString(R.string.rsa_no_exist), MessageTag.ERROR);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            showMessage(context.getString(R.string.rsa_enc_dec_error)+e2, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.rsa_key_pair_verify,functionid = INDEX_SM_RSA_VERIFY)
    private void rsaVerify(){
        try {
            showMessage(context.getString(R.string.rsa_verify), MessageTag.TIP);

            int rslt = smModule.rsaKeyPairVerify(rsaKeyPair.pubkey, rsaKeyPair.prikey);
            if(rslt==0){
                showMessage(context.getString(R.string.rsa_verify_success)+rslt, MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.rsa_verify_fail)+rslt, MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.rsa_verify_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.generate_sm2_key_pair,functionid = INDEX_SM_GENERATE_SM2)
    private void generateSM2Key(){
        try {
            showMessage(context.getString(R.string.gen_sm2), MessageTag.TIP);
            sm2KeyPair = smModule.genSM2KeyPair();
            if(sm2KeyPair!=null){
                byte[] eccPriKey = sm2KeyPair.eccprikey;
                byte[] eccPubKey = sm2KeyPair.eccpubKey;
                if(eccPriKey!=null){
                    showMessage(context.getString(R.string.sm2_pubk_len)+eccPubKey.length, MessageTag.DATA);
                    showMessage(context.getString(R.string.sm2_pubk)+ISOUtils.hexString(eccPubKey), MessageTag.DATA);
                }else{
                    showMessage(context.getString(R.string.sm2_pubk_null), MessageTag.ERROR);
                }

                if(eccPriKey!=null){
                    showMessage(context.getString(R.string.sm2_prik_len)+eccPriKey.length, MessageTag.DATA);
                    showMessage(context.getString(R.string.sm2_prik)+ISOUtils.hexString(eccPriKey), MessageTag.DATA);
                }else{
                    showMessage(context.getString(R.string.sm2_prik_null), MessageTag.ERROR);
                }

            }else{
                showMessage(context.getString(R.string.gen_sm2_fail), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.gen_sm2_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.sm2_public_key_encrypt,functionid = INDEX_SM_SM2_ENCY)
    private void sm2Encry(){
        try {
            if(sm2KeyPair!=null && sm2KeyPair.eccpubKey!=null){
                showMessage(context.getString(R.string.sm2_encry), MessageTag.TIP);
                showMessage(context.getString(R.string.sm2_pubk)+ISOUtils.hexString(sm2KeyPair.eccpubKey), MessageTag.TIP);
                String pubKey = "329F3BA5727CC5B2AC5DDEBEBBF019F5F6E063A29026C046E058B4EC03F4DDB1600EDD15441DF9492378F9DE9BAE0B90F1181DEE2069943E967D493ACF08460B";
                sm2EncryData = smModule.sm2Encrypt(ISOUtils.hex2byte(pubKey), "123456".getBytes("gbk"));
                showMessage(context.getString(R.string.sm2_encry_rslt)+(sm2EncryData==null?null:ISOUtils.hexString(sm2EncryData)), MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.sm2_encry_fali), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.sm2_encry_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.sm2_private_key_decrypt,functionid = INDEX_SM_SM2_DECRY)
    private void sm2Decry(){
        try {
            if(sm2EncryData!=null){
                showMessage(context.getString(R.string.sm2_decry)+ISOUtils.hexString(sm2EncryData), MessageTag.TIP);
                showMessage(context.getString(R.string.sm2_prik)+ISOUtils.hexString(sm2KeyPair.eccprikey), MessageTag.TIP);
                byte[] decryData = smModule.sm2Decrypt(sm2KeyPair.eccprikey, sm2EncryData);
                showMessage(context.getString(R.string.sm2_decry_rslt)+ISOUtils.hexString(decryData), MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.sm2_decry_fail), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.sme_decry_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.generate_sm2_sign_summary,functionid = INDEX_SM_GENERATE_SM2_SIGNDIGEST)
    private void generateSm2Digest(){
        try {
            if(sm2KeyPair!=null){
                showMessage(context.getString(R.string.sm2_gen_digest), MessageTag.TIP);
                byte[] digestDataResourece  = "12345678901234567890123456789012".getBytes("gbk");
                digestDataResourece = ISOUtils.hex2byte("FCD08E4D7B6D6EDA7C8F23F3E984822C5254C002FF5776511D5297E00DCE9BE36B57F135CC93A3CF13650778784E8AA04CA986844FD19B6991E276F3840C1F48");
                sm2KeyPair.eccpubKey = ISOUtils.hex2byte("D1C88164579CDFBF64687BD712082B3E33F0D6B946F00DB2778ED52C5D37D1B4ED3CBC7E2C255EF9B94B1656EF208D3B8F9B4609FAEE0D063FA4023F6BF0E5E0");
                digestData = smModule.sm2GenDigest(new byte[]{0x54, 0x4F, 0x50, 0x53}, digestDataResourece, sm2KeyPair.eccpubKey);
                Log.e("AAA", ISOUtils.hexString(digestData));
                showMessage(context.getString(R.string.sm2_digest)+(digestData==null?null:ISOUtils.hexString(digestData)), MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.sm2_gen_digest_fail), MessageTag.ERROR);;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.sm2_gen_digest_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.sm2_sign,functionid = INDEX_SM_SM2_SIGN)
    private void sm2Sign(){
        try {
            if(sm2KeyPair!=null && digestData!=null){
                showMessage(context.getString(R.string.sm2_sign_start), MessageTag.TIP);
                signedData = smModule.sm2Sign(sm2KeyPair.eccprikey, digestData);
                showMessage(context.getString(R.string.sm2_sign_rslt)+(signedData==null?null:ISOUtils.hexString(signedData)), MessageTag.DATA);
            }else{
                showMessage(context.getString(R.string.sm2_sign_fail), MessageTag.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.sm2_sign_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.sm2_verify_function,functionid = INDEX_SM_SM2_VERIFY)
    private void sm2Verify(){
        try {
            if(signedData!=null && sm2KeyPair!=null){
                showMessage(context.getString(R.string.sm2_sign_verify), MessageTag.TIP);
                sm2KeyPair.eccpubKey = ISOUtils.hex2byte("D1C88164579CDFBF64687BD712082B3E33F0D6B946F00DB2778ED52C5D37D1B4ED3CBC7E2C255EF9B94B1656EF208D3B8F9B4609FAEE0D063FA4023F6BF0E5E0");
                digestData = ISOUtils.hex2byte("DB8B110391296B4CC92C2472A73D281F781D0A9EED14703EAE9C3A7E82CB7752");
                signedData = ISOUtils.hex2byte("1256277AC39D0ED7A53D795CF64AE748B22862DBF2399C7767B8044EC53D52A7D3D8C852DB260ABAB5014831B25558976AE7EF497348D9BC78C2E9A3179211D3");
                int rslt = smModule.sm2Verify(sm2KeyPair.eccpubKey, digestData, signedData);
                if(rslt==0){
                    showMessage(context.getString(R.string.sm2_sign_verify_suc), MessageTag.DATA);
                }else{
                    showMessage(context.getString(R.string.sm2_sign_verify_fail)+rslt, MessageTag.ERROR);
                }
            }else{
                showMessage(context.getString(R.string.sm2_sign_verify_null), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.sm2_sign_verify_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.cal_sm4,functionid = 13)
    private void calSm4(){
        try {
            byte[] result = smModule.calcSM4(ISOUtils.hex2byte("12345678901234567890123456789012"), ISOUtils.hex2byte("01010101010101010101010101010101"),ISOUtils.hex2byte("12345678901234567890123456789012"), (byte)0x02);
            showMessage("SM4 CBC encry result："+(result==null?null:ISOUtils.hexString(result)));
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnname = "generateKey",functionid = 14)
    private void generatePubPriKey(){
        try {
            String[] options = new String[]{"RSA","SM2"};
            DialogUtils.createCustomDialog(context,"generate Pub Pri Key",options,R.layout.dialog_key_operate,new DialogUtils.CustomDialogCallback(){

                @Override
                public void onResult(int id, View dialogView) {
                    if(id<0){
                        return;
                    }
                    EditText editText = dialogView.findViewById(R.id.edit_gen_keyindex);
                    int keyIndex = Integer.parseInt(editText.getText().toString());
                    boolean result;
                    if(id==0){
                        showMessage("generate RSA KEY,index:"+keyIndex);
                        result = smModule.generatePubPriKey(keyIndex,2048,0);
                    }else{
                        showMessage("generate SM2 KEY,index:"+keyIndex);
                        result = smModule.generatePubPriKey(keyIndex,16,1);
                    }
                    showMessage("generate KEY result:"+result);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnname = "load key",functionid = 15)
    private void loadPubPriKey(){
        try {
            showMessage("loas RSA");
            boolean loadRSAResult = smModule.loadPubPriKey(1,ISOUtils.hex2byte("30820122300D06092A864886F70D01010105000382010F003082010A0282010100A77536A5A94D0A47DE2C3269259991BE54492044B9B8E340BDAC082D2DB1139DC23F2CAE4A4D926FD4F9D84F98B63AE59332AB11540B719A06F5AF5625AFE0695AB7FC1C5B0E112C2C3CAD8A33D47F1D524E5C2828C7F6426149E0B7BFB2D437DCDA86898498BA8E23A92864A1D3D72F66F329AB370CB497EE05A83DB920478BBF4345B7AB5A4ECF56A45BA95BA901CB889141ED57A20874B09B8C557370330497ACD933D3011547E8ED71C973DD1121D9D3EA34B00F75E63B2C7CD69259D80FDA724CB7AD8793B86236537A2AB179F681A8E5C1ABAAD6D685388C19D13CB7D0ED359661382AB4CA4A3BA17304138049B33D38504CFF316EA4C87ECF69FB2A610203010001"),ISOUtils.hex2byte("308204BD020100300D06092A864886F70D0101010500048204A7308204A30201000282010100A77536A5A94D0A47DE2C3269259991BE54492044B9B8E340BDAC082D2DB1139DC23F2CAE4A4D926FD4F9D84F98B63AE59332AB11540B719A06F5AF5625AFE0695AB7FC1C5B0E112C2C3CAD8A33D47F1D524E5C2828C7F6426149E0B7BFB2D437DCDA86898498BA8E23A92864A1D3D72F66F329AB370CB497EE05A83DB920478BBF4345B7AB5A4ECF56A45BA95BA901CB889141ED57A20874B09B8C557370330497ACD933D3011547E8ED71C973DD1121D9D3EA34B00F75E63B2C7CD69259D80FDA724CB7AD8793B86236537A2AB179F681A8E5C1ABAAD6D685388C19D13CB7D0ED359661382AB4CA4A3BA17304138049B33D38504CFF316EA4C87ECF69FB2A61020301000102820100739FB03AD45A18662516793C36B50177DC749D6E369A5D773F3F8069C969C1F4A4C1C0151BC2D2009B9A636C1A32811A30F7C43C73BE6F12FE0937A690E10E5F503F473940F9C68BAC83BD0376E41B5CE08EB07D1E236B7ACBF819CC65F591287D3AF9B80C30F466DE62973C5CAC8BA0F441A39CCFA6DB2F282F09CA8C7FA845CC9D5D741DDDF9FD0765ECD1CD4D419BC27D8EDC0B333D01616CE9084CE392A0419C6D920BC0C803AFB809B6CBA458273A0CC7C21E05157FAAEA4CE3B815D117F6D5A15C2286895679ECE13EB4721A0F650E9464CE304D9C7C42A91F71CA00CFA9C305E425E7ED437A55F62906F140738F6F315199CC215BEF615AD26604044102818100D56040E264895906E5F9D203258BA8E3D0DAE6242A00D7F15C31FF8E8CAB14278E92A79D0BCEA29BB41A1999A9C405433FD19E8324A4DCC7FF56511548CC180B9E792608561303550EE4C0E10EA369ABFDB8DE16E5730302CBA32273D707DBF62B0531AF9E56940256E51FCDB22BE50600F1A988118A0BF0918CDE49EB35EFE902818100C8E8C5A1DF4DB19CB093CB42479132DC076D9330BC42C01EF47372996ABBD2990E4208186CE6BE2957A8C99DFE0694FD1F2BD6AC1340E51D4DB838DB7837D06C8E4FF7AB0C60F368818F46B684A61890A0450A47521B39A0559E23C90C34A10DD8C093FE667CE6FEA8C2AD70739A974B4726FD7986DABA88D9216603C48E93B902818100BC05E3671B9C841CB60E9BE735F08954A6B81DD0844AD5FED3752372E3B5E53EAE3DC1017C640B9A14EC62DEAB60161B1A6386C09D4BC1596341169620DDE7DA653CF02CAC5C78B53D388A564765EC3A288BFCF8441C42ECF5B0BD2D42B0A43D99CF2BE74B2993A73417171D5501A500D3582041CCE2CE7EA0637785BF8479F10281807394258DD7EED44CB5F070303307BEE0818D71EDC980051249C2D89C95A073A90560CB5371E2D466E83CFBAFDE615EF8B723FB1D86FE005079538FB20258E99869CE6B46859A88AD084BAA7C79972BEE97A4E022E3833205A0FB96CEA42CEAB2128DC715C553F9776B1283C07B60E5C4B2AC2589F22086CB0F4E7D67917E0C5102818020229DB0461664A8423162ED97DE56DBCD6E0640B8721B3443F5991DF144BDB2267CD61211C478DF2EAFA359B59D2582E1298E2C66EFCE84F345340FD20E6A2D2E7978A539B0E69CABD82ABFCD459FB4E2CD694FA985FDFFF2FCAD79D99FE29624D2F4C63AE6D00DB350AC51FAC7913193E0C40AFCCC731C4741C14DE74E1B04"),0);
            showMessage("loas RSA result:"+loadRSAResult);

//            showMessage("loas Sm2,privateKey and publickey:");
//            boolean loadSM2Result = smModule.loadPubPriKey(1,ISOUtils.hex2byte("E7624C130ADC62B5862484F4D5CAC81DA28B3B415492BB8A0EF18FB79C047145B1FA1F75EDF09B6F0149D3C3ABA4022566428E66B1C159766216C79266F126C2"),ISOUtils.hex2byte("EE87A2BF10A7096E116C903C22546366A70C609E3C3F88E202394E9DB568D8CF"),1);
//            showMessage("loas Sm2 reslut:"+loadSM2Result);

        }catch (Exception e){
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e, MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnname = "get public key",functionid = 16)
    private void getPulicbKey(){
        try {
            String[] options = new String[]{"RSA","SM2"};
            DialogUtils.createCustomDialog(context,"generate Pub Pri Key",options,R.layout.dialog_key_operate,new DialogUtils.CustomDialogCallback(){

                @Override
                public void onResult(int id, View dialogView) {
                    if(id<0){
                        return;
                    }
                    EditText editText = dialogView.findViewById(R.id.edit_gen_keyindex);
                    int keyIndex = Integer.parseInt(editText.getText().toString());
                    byte[] result;
                    if(id==0){
                        showMessage("get RSA public KEY,index:"+keyIndex);
                        result = smModule.getPulicbKey(keyIndex,0);
                        showMessage("public KEY length:"+(result==null?0:result.length));
                        showMessage("public KEY:"+(result==null?null:ISOUtils.hexString(result)));

                        dataToDecry =  encryptByPublicKey(result,ISOUtils.hex2byte("11111111111111111111111111111111"));
                        showMessage("dataToDecry:"+(dataToDecry==null?0:ISOUtils.hexString(dataToDecry)));

                        showMessage("RSA private ket decry,data:"+(dataToDecry==null?null:ISOUtils.hexString(dataToDecry)));
                        result = smModule.calPrivateKey(keyIndex,1,dataToDecry,0);
                        showMessage("RSA private ket decrypt,result:"+(result==null?null:ISOUtils.hexString(result)));
                    }else{
                        showMessage("get SM2 public KEY,index:"+keyIndex);
                        result = smModule.getPulicbKey(keyIndex,1);
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e, MessageTag.ERROR);
        }
    }


    public static byte[] encryptByPublicKey(byte[] publicKey, byte[] data) {
        try {
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(publicKey);
            KeyFactory keyF = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyF.generatePublic(pubX509);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @MethodGridEntity(btnname = "calPrivateKey",functionid = 17)
    private void calPrivateKey(){
        try {
            String[] options = new String[]{"RSA","SM2"};
            DialogUtils.createCustomDialog(context,"calPrivateKey",options,R.layout.dialog_key_operate,new DialogUtils.CustomDialogCallback(){

                @Override
                public void onResult(int id, View dialogView) {
                    if(id<0){
                        return;
                    }
                    EditText editText = dialogView.findViewById(R.id.edit_gen_keyindex);
                    int keyIndex = Integer.parseInt(editText.getText().toString());
                    EditText dataOper = dialogView.findViewById(R.id.edit_data_oper);
                    String data = dataOper.getText().toString();
                    byte[] result;
                    if(id==0){
                        showMessage("calPrivateKey,RSA key index:"+keyIndex);
                        showMessage("RSA private ket encry,data:"+data);
                        result = smModule.calPrivateKey(keyIndex,0,ISOUtils.hex2byte(data),0);
                        showMessage("RSA private ket encry,result:"+(result==null?null:ISOUtils.hexString(result)));

//                        showMessage("RSA private ket decry,data:"+(dataToDecry==null?null:ISOUtils.hexString(dataToDecry)));
//                        result = smModule.calPrivateKey(keyIndex,1,dataToDecry,0);
//                        showMessage("RSA private ket decrypt,result:"+(result==null?null:ISOUtils.hexString(result)));
                    }else{
                        showMessage("calPrivateKey,SM2 key index:"+keyIndex);
                        showMessage("SM2 private key decry data:"+data);
                        result = smModule.calPrivateKey(keyIndex,1,ISOUtils.hex2byte(data),1);
                        showMessage("SM2 private key decry result:"+(result==null?null:ISOUtils.hexString(result)));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e, MessageTag.ERROR);
        }
    }

}
