package com.newland.sdkdemo.utils;

public class EmvErrorCode {
    public static String getErrorDescribe(int errorCode) {
        if (errorCode == (0)) {
            return "(success)";
        } else if (errorCode == (-1)) {
            return "(Failed to read aid configuration)";
        } else if (errorCode == (-2)) {
            return "(Failed to read aid list)";
        } else if (errorCode == (-3)) {
            return "(IC card unable to power on)";
        } else if (errorCode == (-4)) {
            return "(IC card not support instruction)";
        } else if (errorCode == (-5)) {
            return "(Application lock)";
        } else if (errorCode == (-6)) {
            return "(Can't find supported applications)";
        } else if (errorCode == (-7)) {
            return "(Quit the transaction)";
        } else if (errorCode == (-8)) {
            return "(Application selection failed)";
        } else if (errorCode == (-9)) {
            return "(Application initialization failed)";
        } else if (errorCode == (-10)) {
            return "(Failed to read application data)";
        } else if (errorCode == (-11)) {
            return "(Offline data authentication failed)";
        } else if (errorCode == (-12)) {
            return "(Process limit failed)";
        } else if (errorCode == (-13)) {
            return "(Cardholder authentication failed)";
        } else if (errorCode == (-14)) {
            return "(Terminal risk management failed)";
        } else if (errorCode == (-15)) {
            return "(Terminal behavior analysis failed)";
        } else if (errorCode == (-16)) {
            return "(Unsupported service)";
        } else if (errorCode == (-17)) {
            return "(No random number)";
        } else if (errorCode == (-18)) {
            return "(Card lock)";
        } else if (errorCode == (-19)) {
            return "(GEN AC execution failed";
        } else if (errorCode == (-20)) {
            return "(Save key configuration failed)";
        } else if (errorCode == (-30)) {
            return "(RF card preprocessing failed)";
        } else if (errorCode == (-31)) {
            return "(Operation timeout)";
        } else if (errorCode == (-32)) {
            return "(Inserted IC card detected during RF card finder)";
        } else if (errorCode == (-33)) {
            return "(Too many AIDs)";
        } else if (errorCode == (-34)) {
            return "empty aid";
        } else if (errorCode == (-500)) {
            return "reserve for L3-start";
        } else if (errorCode == (-800)) {
            return "reserve for L3-end";
        } else if (errorCode == (-900)) {
            return "(AID configuration file benchmark error code)";
        } else if (errorCode == (-900 - 1)) {
            return "(AID configuration file open failed)";
        } else if (errorCode == (-900 - 2)) {
            return "(AID configuration file read failed)";
        } else if (errorCode == (-900 - 3)) {
            return "(AID configuration file write failed)";
        } else if (errorCode == (-900 - 4)) {
            return "(AID configuration file version error)";
        } else if (errorCode == (-900 - 5)) {
            return "(AID configuration file Can't get the required AID)";
        } else if (errorCode == (-900 - 6)) {
            return "(AID configuration file Tlv data parsing failed)";
        } else if (errorCode == (-900 - 7)) {
            return "(Use wrong API interface)";
        } else if (errorCode == (-1000)) {
            return "(ICC Apdu benchmark error code)";
        } else if (errorCode == (-1000 - 1)) {
            return "(apdu interactive data is empty";
        } else if (errorCode == (-1100)) {
            return "(Kernel basic operation benchmark error code";
        } else if (errorCode == (-1100 - 1)) {
            return "(Get POS time error)";
        } else if (errorCode == (-1100 - 2)) {
            return "(READ FINAL PARAM error)";
        } else if (errorCode == (-1100 - 3)) {
            return "(Get random number error)";
        } else if (errorCode == (-1200)) {
            return "(Data cache benchmark error code)";
        } else if (errorCode == (-1200 - 1)) {
            return "(Data buffer not enough storage)";
        } else if (errorCode == (-1200 - 2)) {
            return "(The label is unique and cannot be overwritten if the length is greater than zero)";
        } else if (errorCode == (-1300)) {
            return "(Application select benchmark error code)";
        } else if (errorCode == (-1300 - 1)) {
            return "(application select AID list failed)";
        } else if (errorCode == (-1300 - 2)) {
            return "(Application select cancle the transaction)";
        } else if (errorCode == (-1300 - 3)) {
            return "(FCI data format error)";
        } else if (errorCode == (-1300 - 4)) {
            return "(FCI data without 6F)";
        } else if (errorCode == (-1300 - 5)) {
            return "(FCI data without 84)";
        } else if (errorCode == (-1300 - 6)) {
            return "(FCI data without A5)";
        } else if (errorCode == (-1300 - 7)) {
            return "(FCI data error 9F38)";
        } else if (errorCode == (-1300 - 8)) {
            return "(FCI data tagBF0C repeat)";
        } else if (errorCode == (-1300 - 9)) {
            return "(Application selection return transaction)";
        } else if (errorCode == (-1300 - 10)) {
            return "(FCI data tag50 repeat)";
        } else if (errorCode == (-1300 - 11)) {
            return "(FCI data repeat)";
        } else if (errorCode == (-1300 - 12)) {
            return "(FCI data TLV parsing failed)";
        } else if (errorCode == (-1300 - 13)) {
            return "(FCI data without tag 6F)";
        } else if (errorCode == (-1300 - 14)) {
            return "(FCI data without tag 84)";
        } else if (errorCode == (-1300 - 15)) {
            return "(FCI data without tag A5)";
        } else if (errorCode == (-1300 - 16)) {
            return "(The tag sequence is wrong.";
        } else if (errorCode == (-1300 - 17)) {
            return "(FCI data without tag BF0C)";
        } else if (errorCode == (-1300 - 18)) {
            return "(FCI data tag 8F0C parsing failed)";
        } else if (errorCode == (-1300 - 19)) {
            return "(final selection, return DF Name  not the same as the AID of command)";
        } else if (errorCode == (-1300 - 20)) {
            return "(Wrong transaction type)";
        } else if (errorCode == (-1300 - 21)) {
            return "(PPSE command returned failure)";
        } else if (errorCode == (-1300 - 22)) {
            return "(Select the next AID)";
        } else if (errorCode == (-1300 - 23)) {
            return "(Discover zip aid special treatment for ppse)";
        } else if (errorCode == (-1300 - 24)) {
            return "(FCI data without the value of tag 84)";
        } else if (errorCode == (-1300 - 25)) {
            return "(DPAS ppse only returns dpas aid does not return zip aid)";
        } else if (errorCode == (-1300 - 27)) {
            return "(contactless transaction amount  exceeds limit)";
        } else if (errorCode == (-1300 - 28)) {
            return "(when Amount zero, Set the Contactless Application Not Allowed)";
        } else if (errorCode == (-1300 - 29)) {
            return "(when Amount zero, shall set go online, but terminal offline only)";
        } else if (errorCode == (-1300 - 35)) {
            return "(Application selection tag61 is invalid)";
        } else if (errorCode == (-1300 - 36)) {
            return "(Application selection tag4F is invalid)";
        } else if (errorCode == (-1300 - 37)) {
            return "(Application selection tag4F partial matching incomplete)";
        } else if (errorCode == (-1300 - 38)) {
            return "(JCB FCI format error)";
        } else if (errorCode == (-1300 - 39)) {
            return "(JCB FCI 84 error)";
        } else if (errorCode == (-1300 - 40)) {
            return "(FCI without 50,)";
        } else if (errorCode == (-1300 - 41)) {
            return "(JCB FCI without 9F38)";
        } else if (errorCode == (-1300 - 42)) {
            return "(JCB FCI 9F28 is empty)";
        } else if (errorCode == (-1300 - 43)) {
            return "(JCB FCI failed to find proprietary data)";
        } else if (errorCode == (-1300 - 44)) {
            return "(JCB Torn transaction is Legacy Mode)";
        } else if (errorCode == (-1300 - 45)) {
            return "(MCCS enforces data loss)";
        } else if (errorCode == (-1300 - 50)) {
            return "(Application selection returns 6300,State of non-volatile memory changed; authentication failed)";
        } else if (errorCode == (-1300 - 51)) {
            return "(Application selection returns 63C1)";
        } else if (errorCode == (-1300 - 52)) {
            return "(Application selection returns 6983,Command not allowed; authentication method blocked)";
        } else if (errorCode == (-1300 - 53)) {
            return "(Application selection returns 6984,Command not allowed; reference data not usable)";
        } else if (errorCode == (-1300 - 54)) {
            return "(Application selection returns 6985,Command not allowed; conditions of use not satisfied)";
        } else if (errorCode == (-1300 - 55)) {
            return "(Application selection returns 6A82,Wrong parameters P1-P2; file or application not found)";
        } else if (errorCode == (-1300 - 56)) {
            return "(Application selection returns 6A83,Wrong parameters P1-P2; record not found)";
        } else if (errorCode == (-1300 - 57)) {
            return "(Application selection returns 6A88,Reference data (data objects) not found)";
        } else if (errorCode == (-1300 - 58)) {
            return "(Application selection returns 6400)";
        } else if (errorCode == (-1300 - 59)) {
            return "(Application selection returns 6500)";
        } else if (errorCode == (-1300 - 60)) {
            return "(Application selection returns 9001)";
        } else if (errorCode == (-1300 - 61)) {
            return "(Application selection apdu communication failed)";
        } else if (errorCode == (-1300 - 70)) {
            return "(JCB benchmark error code)";
        } else if (errorCode == (-1300 - 70 - 1)) {
            return "(JCB configured to be empty)";
        } else if (errorCode == (-1300 - 70 - 2)) {
            return "(JCB Legacy mode is not supported)";
        } else if (errorCode == (-1400)) {
            return "(Application initialization benchmark error code)";
        } else if (errorCode == (-1400 - 1)) {
            return "(Application initialization PDOL packaging failed)";
        } else if (errorCode == (-1400 - 2)) {
            return "(Application initialization return value is not equal to 9000)";
        } else if (errorCode == (-1400 - 3)) {
            return "(Application initialization returns 6984)";
        } else if (errorCode == (-1400 - 4)) {
            return "(Application initialization returns TLV parsing error)";
        } else if (errorCode == (-1400 - 5)) {
            return "(Application initialization returns 80 template length error)";
        } else if (errorCode == (-1400 - 6)) {
            return "(Application initialization returns 77 template without AIP)";
        } else if (errorCode == (-1400 - 7)) {
            return "(Application initialization returns AIP length error)";
        } else if (errorCode == (-1400 - 8)) {
            return "(Application initialization returns 77 template without AFL)";
        } else if (errorCode == (-1400 - 9)) {
            return "(Application initialization returns AFL length error)";
        } else if (errorCode == (-1400 - 10)) {
            return "(Application initialization returns invalid tag)";
        } else if (errorCode == (-1400 - 11)) {
            return "(Application initialization without PDOL)";
        } else if (errorCode == (-1400 - 12)) {
            return "(Application initialize pboc return 80 template)";
        } else if (errorCode == (-1400 - 13)) {
            return "(Application initialization card is not supported)";
        } else if (errorCode == (-1400 - 14)) {
            return "(Cancel electronic cash transactions)";
        } else if (errorCode == (-1400 - 15)) {
            return "(Pure electronic cash card, but does not support e-cash)";
        } else if (errorCode == (-1400 - 16)) {
            return "(Application initialization returns 6984< 20120911 zhengel 6984 special treatment)";
        } else if (errorCode == (-1400 - 17)) {
            return "(Application initialization returns 6985 <20160330 fangjt 6985 directly terminate the transaction)";
        } else if (errorCode == (-1400 - 18)) {
            return "(Application initialization returns 6283)";
        } else if (errorCode == (-1400 - 19)) {
            return "(Application initialization returns 6300)";
        } else if (errorCode == (-1400 - 20)) {
            return "(Application initialization returns 63C1)";
        } else if (errorCode == (-1400 - 21)) {
            return "(Application initialization returns 6983)";
        } else if (errorCode == (-1400 - 22)) {
            return "(Application initialization returns 6986)";
        } else if (errorCode == (-1400 - 23)) {
            return "(Application initialization returns 9001)";
        } else if (errorCode == (-1400 - 24)) {
            return "(Application initialization returns 6A81)";
        } else if (errorCode == (-1400 - 25)) {
            return "(Application initialization returns 6A82)";
        } else if (errorCode == (-1400 - 26)) {
            return "(Application initialization returns 6A83)";
        } else if (errorCode == (-1400 - 27)) {
            return "(Application initialization returns 6A88)";
        } else if (errorCode == (-1400 - 28)) {
            return "(Application initialization returns 6500)";
        } else if (errorCode == (-1400 - 29)) {
            return "(Application initialization returns 6400)";
        } else if (errorCode == (-1400 - 30)) {
            return "(Application initialization returns 9408)";
        } else if (errorCode == (-1400 - 31)) {
            return "(Application initialization returns ATC error)";
        } else if (errorCode == (-1400 - 32)) {
            return "(Application initialization returns AC error)";
        } else if (errorCode == (-1400 - 33)) {
            return "(Application initialization returns 9F10 error)";
        } else if (errorCode == (-1400 - 34)) {
            return "(Application initialization returns 57 error)";
        } else if (errorCode == (-1400 - 35)) {
            return "(Application initialization returns AFL error)";
        } else if (errorCode == (-1400 - 36)) {
            return "(Application initialization returns 5F20 error)";
        } else if (errorCode == (-1400 - 37)) {
            return "(Application initialization without 9F66)";
        } else if (errorCode == (-1400 - 38)) {
            return "(The insert card is detected during the contactless application initialization process)";
        } else if (errorCode == (-1400 - 39)) {
            return "(Application initialization 9F27 error)";
        } else if (errorCode == (-1400 - 40)) {
            return "(Application initialization APP does not support)";
        } else if (errorCode == (-1400 - 41)) {
            return "(Pure electronic cash card but requires online)";
        } else if (errorCode == (-1400 - 42)) {
            return "(Pure electronic cash card but requires CVM)";
        } else if (errorCode == (-1400 - 43)) {
            return "(Magnetic stripe card is detected during the contactless application initialization process.)";
        } else if (errorCode == (-1400 - 44)) {
            return "(Application initialization returns ATC error)";
        } else if (errorCode == (-1400 - 45)) {
            return "(Application initialization failed to save data)";
        } else if (errorCode == (-1400 - 46)) {
            return "(Application initialization returns 77 template AFL data duplication)";
        } else if (errorCode == (-1400 - 47)) {
            return "(Application initialization returns 80 template AFL data duplication)";
        } else if (errorCode == (-1400 - 48)) {
            return "(Application initialization gets 81)";
        } else if (errorCode == (-1400 - 49)) {
            return "(Application initialization returns 77 template AIP data duplication)";
        } else if (errorCode == (-1400 - 50)) {
            return "(Application initialization returns 80 template AIP data duplication)";
        } else if (errorCode == (-1400 - 51)) {
            return "(Application initialization returns 9F01 error)";
        } else if (errorCode == (-1400 - 52)) {
            return "(Application initialization returns 9F69 error)";
        } else if (errorCode == (-1400 - 53)) {
            return "(Application initialization failed to save data)";
        } else if (errorCode == (-1400 - 54)) {
            return "(Application initialization tlv data parsing error)";
        } else if (errorCode == (-1400 - 55)) {
            return "(Application initialization card data lose)";
        } else if (errorCode == (-1400 - 56)) {
            return "(Application initialization AFL format error)";
        } else if (errorCode == (-1400 - 57)) {
            return "(Application initialization tag repeat)";
        } else if (errorCode == (-1400 - 58)) {
            return "(JCB application initialization without return AIP)";
        } else if (errorCode == (-1400 - 59)) {
            return "(JCB application initialization without return SFI)";
        } else if (errorCode == (-1400 - 60)) {
            return "(JCB application initialization is not 77 or 80 template)";
        } else if (errorCode == (-1400 - 61)) {
            return "(JCB application initializes SFI error)";
        } else if (errorCode == (-1400 - 62)) {
            return "(Application initialization cancel transaction)";
        } else if (errorCode == (-1400 - 63)) {
            return "(Interac FFI does not support contactless)";
        } else if (errorCode == (-1400 - 64)) {
            return "(Interac FFI does not support mobile phones)";
        } else if (errorCode == (-1400 - 65)) {
            return "(Interac FFI value error)";
        } else if (errorCode == (-1400 - 66)) {
            return "(interac no CTI)";
        } else if (errorCode == (-1400 - 67)) {
            return "(interac CTI lenth wrong)";
        } else if (errorCode == (-1400 - 68)) {
            return "(Interac CTI requires a transfer interface)";
        } else if (errorCode == (-1400 - 69)) {
            return "(Interac CTI requires a transfer interface, but this machine does not support,viewing other machines)";
        } else if (errorCode == (-1400 - 70)) {
            return "(Interac CTI requires a transfer interface, which is not supported by this machine and other machines.)";
        } else if (errorCode == (-1400 - 71)) {
            return "(Interac exceeds the maximum number of try again)";
        } else if (errorCode == (-1400 - 72)) {
            return "(Rupay second Remove the card and put it back later is mismatch)";
        } else if (errorCode == (-1400 - 72)) {
            return "(GPO APDU response exception)";
        } else if (errorCode == (-1500)) {
            return "(Read record benchmark error code)";
        } else if (errorCode == (-1500 - 1)) {
            return "(Read record SFI error)";
        } else if (errorCode == (-1500 - 2)) {
            return "(Read record first record error)";
        } else if (errorCode == (-1500 - 3)) {
            return "(Read record SFI record range error)";
        } else if (errorCode == (-1500 - 4)) {
            return "(Read record return code error)";
        } else if (errorCode == (-1500 - 5)) {
            return "(Read record TLV parsing error)";
        } else if (errorCode == (-1500 - 6)) {
            return "(Read record without return tag 5A)";
        } else if (errorCode == (-1500 - 7)) {
            return "(Read record without return tag 8C)";
        } else if (errorCode == (-1500 - 8)) {
            return "(Read record without return tag 8D)";
        } else if (errorCode == (-1500 - 9)) {
            return "(Read record tag5F24 error)";
        } else if (errorCode == (-1500 - 10)) {
            return "(Read record tag5F25 error)";
        } else if (errorCode == (-1500 - 11)) {
            return "(Failed to read record storage data)";
        } else if (errorCode == (-1500 - 12)) {
            return "(readrecord return Terminal Data,so terminate)";
        } else if (errorCode == (-1500 - 13)) {
            return "(The last readrecord cmd failed (this macro value is immutable))";
        } else if (errorCode == (-1500 - 14)) {
            return "(Read record without return tag 57)";
        } else if (errorCode == (-1500 - 15)) {
            return "(Read record without return tag 5F20)";
        } else if (errorCode == (-1500 - 21)) {
            return "(Read record without return tag 9F74)";
        } else if (errorCode == (-1500 - 22)) {
            return "(Read record without return tag 9F79)";
        } else if (errorCode == (-1500 - 23)) {
            return "(Read record without return tag 9F36)";
        } else if (errorCode == (-1500 - 24)) {
            return "(Read record returns tag 9F36 error)";
        } else if (errorCode == (-1500 - 31)) {
            return "(Transaction date expired)";
        } else if (errorCode == (-1500 - 32)) {
            return "(The date of transaction is not valid)";
        } else if (errorCode == (-1500 - 50)) {
            return "(Read record returns to 6283)";
        } else if (errorCode == (-1500 - 51)) {
            return "(Read record returns to 6300)";
        } else if (errorCode == (-1500 - 52)) {
            return "(Read record returns to 63C1)";
        } else if (errorCode == (-1500 - 53)) {
            return "(Read record returns to 6983)";
        } else if (errorCode == (-1500 - 54)) {
            return "(Read record returns to 6984)";
        } else if (errorCode == (-1500 - 55)) {
            return "(Read record returns to 6985)";
        } else if (errorCode == (-1500 - 56)) {
            return "(Read record returns to 6A81)";
        } else if (errorCode == (-1500 - 57)) {
            return "(Read record returns to 6A82)";
        } else if (errorCode == (-1500 - 58)) {
            return "(Read record returns to 6A83)";
        } else if (errorCode == (-1500 - 59)) {
            return "(Read record returns to 6A88)";
        } else if (errorCode == (-1500 - 60)) {
            return "(Read record returns to 6400)";
        } else if (errorCode == (-1500 - 61)) {
            return "(Read record returns to 6500)";
        } else if (errorCode == (-1500 - 62)) {
            return "(Read record returns to 9001)";
        } else if (errorCode == (-1500 - 63)) {
            return "(Read record returns 5A repeat)";
        } else if (errorCode == (-1500 - 64)) {
            return "(Read record returns 5F24 repeat)";
        } else if (errorCode == (-1500 - 65)) {
            return "(Read record returns 57 repeat)";
        } else if (errorCode == (-1500 - 66)) {
            return "(Read record cancel transaction)";
        } else if (errorCode == (-1500 - 67)) {
            return "(The date of the card returned by the read record is incorrect)";
        } else if (errorCode == (-1500 - 68)) {
            return "(5A and 57 returned by the read record do not match)";
        } else if (errorCode == (-1500 - 69)) {
            return "(Read record without return tag 9F08)";
        } else if (errorCode == (-1500 - 70)) {
            return "(Read record without return tag 9F02)";
        } else if (errorCode == (-1500 - 71)) {
            return "(Read records exceed the maximum limit)";
        } else if (errorCode == (-1500 - 72)) {
            return "(Read record returns tag 9F4A error)";
        } else if (errorCode == (-1500 - 73)) {
            return "(Read record without return tag 9F4A)";
        } else if (errorCode == (-1500 - 74)) {
            return "(Read record without return tag 8F)";
        } else if (errorCode == (-1500 - 75)) {
            return "(Read record without return tag 90)";
        } else if (errorCode == (-1500 - 76)) {
            return "(Read record without return tag 9F32)";
        } else if (errorCode == (-1500 - 77)) {
            return "(Read record without return tag 93)";
        } else if (errorCode == (-1500 - 78)) {
            return "(Read record without return tag 9F46)";
        } else if (errorCode == (-1500 - 79)) {
            return "(Read record without return tag 9F47)";
        } else if (errorCode == (-1500 - 80)) {
            return "(Capk does not support)";
        } else if (errorCode == (-1500 - 81)) {
            return "(ICC return CardReader Data,so terminate)";
        } else if (errorCode == (-1500 - 82)) {
            return "(Read record without return tag 57)";
        } else if (errorCode == (-1500 - 83)) {
            return "(Read record return data format error)";
        } else if (errorCode == (-1500 - 84)) {
            return "(Read record without return tag 5F24)";
        } else if (errorCode == (-1500 - 85)) {
            return "(Read record TAG 9F42 INVALID";
        } else if (errorCode == (-1500 - 86)) {
            return "(Read record TAG 5F25 INVALID";
        } else if (errorCode == (-1500 - 87)) {
            return "(Read record TAG 5A INVALID";
        } else if (errorCode == (-1500 - 88)) {
            return "(Read record TAG 9F07 INVALID";
        } else if (errorCode == (-1500 - 89)) {
            return "(Read record TAG 5F20 INVALID";
        } else if (errorCode == (-1500 - 90)) {
            return "(Read record TAG 9F0D INVALID";
        } else if (errorCode == (-1500 - 91)) {
            return "(Read record TAG 9F0E INVALID";
        } else if (errorCode == (-1500 - 92)) {
            return "(Read record TAG 9F0F INVALID";
        } else if (errorCode == (-1500 - 93)) {
            return "(Read record TAG 5F34 INVALID";
        } else if (errorCode == (-1500 - 94)) {
            return "(Read record TAG 9F11 INVALID";
        } else if (errorCode == (-1500 - 95)) {
            return "(Read record TAG 5F28 INVALID";
        } else if (errorCode == (-1500 - 96)) {
            return "(Read record TAG 8F INVALID";
        } else if (errorCode == (-1500 - 97)) {
            return "(Read record without return tag 9F6D)";
        } else if (errorCode == (-1600)) {
            return "(Data authentication benchmark error code)";
        } else if (errorCode == (-1600 - 1)) {
            return "(Ic card data loss)";
        } else if (errorCode == (-1600 - 2)) {
            return "(Data Authentication Certificate Length Error)";
        } else if (errorCode == (-1600 - 3)) {
            return "(Data authentication RSA failed to recover public key)";
        } else if (errorCode == (-1600 - 4)) {
            return "(Data authentication recovery data header error)";
        } else if (errorCode == (-1600 - 5)) {
            return "(The second byte of the data authentication certificate is wrong)";
        } else if (errorCode == (-1600 - 6)) {
            return "(Second to last byte of the data authentication certificate is wrong)";
        } else if (errorCode == (-1600 - 7)) {
            return "(Data authentication hash value check does not match)";
        } else if (errorCode == (-1600 - 8)) {
            return "(Data authentication algorithm identification error)";
        } else if (errorCode == (-1600 - 9)) {
            return "(Data Authentication Certificate Module Length error)";
        } else if (errorCode == (-1600 - 10)) {
            return "(Data Authentication Certificate expires)";
        } else if (errorCode == (-1600 - 11)) {
            return "(Data authentication and certification center public key blacklist)";
        } else if (errorCode == (-1600 - 12)) {
            return "(Data authentication SSAD length error)";
        } else if (errorCode == (-1600 - 13)) {
            return "(Data authentication recovery SSAD error)";
        } else if (errorCode == (-1600 - 14)) {
            return "(Data authentication 9F4A error)";
        } else if (errorCode == (-1600 - 15)) {
            return "(Data authentication without default DDOL)";
        } else if (errorCode == (-1600 - 16)) {
            return "(Data authentication without tag 9F37)";
        } else if (errorCode == (-1600 - 17)) {
            return "(Data authentication DDOL package error)";
        } else if (errorCode == (-1600 - 18)) {
            return "(Data authentication SSAD length error)";
        } else if (errorCode == (-1600 - 19)) {
            return "(Data authentication without issuing bank public key)";
        } else if (errorCode == (-1600 - 20)) {
            return "(Data authentication without IC card public key)";
        } else if (errorCode == (-1600 - 21)) {
            return "(Data Authentication Acquisition Random Number error)";
        } else if (errorCode == (-1600 - 22)) {
            return "(Data Authentication Recovery RSA Data error)";
        } else if (errorCode == (-1600 - 23)) {
            return "(Data authentication without tag 9F4B)";
        } else if (errorCode == (-1600 - 24)) {
            return "(Data Authentication Recovery SDAD Error)";
        } else if (errorCode == (-1600 - 25)) {
            return "(Data Authentication SCDAD Length Error)";
        } else if (errorCode == (-1600 - 26)) {
            return "(Data Authentication Recovery SCDAD Error)";
        } else if (errorCode == (-1600 - 27)) {
            return "(Data Authentication CID Value Mismatch)";
        } else if (errorCode == (-1600 - 28)) {
            return "(Data authentication hash check 1 error)";
        } else if (errorCode == (-1600 - 29)) {
            return "(Data authentication hash check 2 error)";
        } else if (errorCode == (-1600 - 30)) {
            return "(Data Authentication Reading Record Error)";
        } else if (errorCode == (-1600 - 31)) {
            return "(Data Authentication Card Number Mismatch)";
        } else if (errorCode == (-1600 - 32)) {
            return "(Data Authentication and Authentication Center Public Key Mismatch)";
        } else if (errorCode == (-1600 - 33)) {
            return "(Data authentication without tag 9F36)";
        } else if (errorCode == (-1600 - 34)) {
            return "(Data Authentication FDDA Version does not support)";
        } else if (errorCode == (-1600 - 35)) {
            return "(Data Authentication FDDA 9F69 Length Error)";
        } else if (errorCode == (-1600 - 36)) {
            return "(Data Authentication national secret Elliptic Parameter Identification Error)";
        } else if (errorCode == (-1600 - 40)) {
            return "(Data authentication SM2 authentication signature error)";
        } else if (errorCode == (-1600 - 41)) {
            return "(Data authentication 9F69 error)";
        } else if (errorCode == (-1600 - 42)) {
            return "(Data authentication FDDA version error)";
        } else if (errorCode == (-1600 - 43)) {
            return "(Data authentication ATC error)";
        } else if (errorCode == (-1600 - 44)) {
            return "(Data authentication forces data errors)";
        } else if (errorCode == (-1600 - 45)) {
            return "(Data authentication without tag 5A)";
        } else if (errorCode == (-1600 - 46)) {
            return "(the length of ICC Dynamic Data is less)";
        } else if (errorCode == (-1600 - 47)) {
            return "(paypass CDA RRP not match)";
        } else if (errorCode == (-1600 - 48)) {
            return "(Hash indication of data authentication error)";
        } else if (errorCode == (-1600 - 49)) {
            return "(Data authentication without hash algorithm)";
        } else if (errorCode == (-1600 - 50)) {
            return "(Data authentication without return tag9F4B or tag92)";
        } else if (errorCode == (-1600 - 51)) {
            return "(Data authentication DDA failed)";
        } else if (errorCode == (-1600 - 52)) {
            return "(Without 92 or 9F48)";
        } else if (errorCode == (-1700)) {
            return "(Cardholder Certification benchmark Error Code)";
        } else if (errorCode == (-1700 - 1)) {
            return "(tag8E length error)";
        } else if (errorCode == (-1700 - 2)) {
            return "(Input offline pin failed)";
        } else if (errorCode == (-1700 - 3)) {
            return "(Input online pin failed)";
        } else if (errorCode == (-1700 - 4)) {
            return "(Card AIP does not support CVM)";
        } else if (errorCode == (-1700 - 5)) {
            return "(Without CVM list)";
        } else if (errorCode == (-1700 - 6)) {
            return "(Without CVM rules)";
        } else if (errorCode == (-1800)) {
            return "(Terminal and card behavior analysis benchmark error code)";
        } else if (errorCode == (-1800 - 1)) {
            return "(GAC response error)";
        } else if (errorCode == (-1800 - 2)) {
            return "(GAC CDOL data pack error)";
        } else if (errorCode == (-1800 - 3)) {
            return "(GAC return code error)";
        } else if (errorCode == (-1800 - 4)) {
            return "(GAC returns data parsing error)";
        } else if (errorCode == (-1800 - 5)) {
            return "Tag 80 templet cda requested and AAC not returned";
        } else if (errorCode == (-1800 - 6)) {
            return "(GAC Returns Tag Length Error)";
        } else if (errorCode == (-1800 - 7)) {
            return "(GAC returns 77 templates without returning 9F27)";
        } else if (errorCode == (-1800 - 8)) {
            return "(GAC returns 77 templates without returning 9F36)";
        } else if (errorCode == (-1800 - 9)) {
            return "(GAC returns 77 templates without returning 9F4B)";
        } else if (errorCode == (-1800 - 10)) {
            return "(GAC returns not 77 or 80 templates)";
        } else if (errorCode == (-1800 - 11)) {
            return "(CID request AAR returned by GAC)";
        } else if (errorCode == (-1800 - 12)) {
            return "(GAC returns the wrong CID)";
        } else if (errorCode == (-1800 - 13)) {
            return "(GAC returns 77 templates without returning 9F26)";
        } else if (errorCode == (-1800 - 14)) {
            return "(GAC without return 9F10)";
        } else if (errorCode == (-1800 - 15)) {
            return "(GAC returns zero data)";
        } else if (errorCode == (-1800 - 16)) {
            return "(DRDOL Data Pack Failed)";
        } else if (errorCode == (-1800 - 17)) {
            return "(RAC response code is not 9000)";
        } else if (errorCode == (-1800 - 18)) {
            return "(GAC returns 80 templates with 9F36)";
        } else if (errorCode == (-1800 - 20)) {
            return "(GAC Error in Electronic Cash)";
        } else if (errorCode == (-1800 - 21)) {
            return "(Failed to obtain EC balance)";
        } else if (errorCode == (-1800 - 22)) {
            return "(EC Balance not enough)";
        } else if (errorCode == (-1800 - 23)) {
            return "(Pure electronic cash card requires EC online but refuse)";
        } else if (errorCode == (-1800 - 24)) {
            return "(EC balance < transaction amount + threshold, resulting in online)";
        } else if (errorCode == (-1800 - 25)) {
            return "(GAC returns 77 templates without returning 81)";
        } else if (errorCode == (-1800 - 26)) {
            return "(GAC returns 77 templates without returning 9F01)";
        } else if (errorCode == (-1800 - 27)) {
            return "(GAC returns CID length error)";
        } else if (errorCode == (-1800 - 28)) {
            return "(GAC returns 9F10 format error)";
        } else if (errorCode == (-1800 - 29)) {
            return "(GAC returns 77 templates without returning tag C5)";
        } else if (errorCode == (-1800 - 30)) {
            return "(Neither MCCS GPO nor GAC returned to tag C5)";
        } else if (errorCode == (-1800 - 31)) {
            return "(MCCS GPO and GAC return tage C5 CVM different ways)";
        } else if (errorCode == (-1800 - 32)) {
            return "(MCCS GAC apdu without return, need to enter torn transaction)";
        } else if (errorCode == (-1800 - 33)) {
            return "(MCCS GAC without 9F4B but has 9F26)";
        } else if (errorCode == (-1800 - 34)) {
            return "(First GAC CDOL package failed)";
        } else if (errorCode == (-1800 - 35)) {
            return "(The 77 template format returned by JCB GAC is wrong.)";
        } else if (errorCode == (-1800 - 36)) {
            return "(JCB failed to store the label returned by GAC)";
        } else if (errorCode == (-1800 - 37)) {
            return "(JCB storage GAC without return CID)";
        } else if (errorCode == (-1800 - 38)) {
            return "(JCB GAC without return ATC)";
        } else if (errorCode == (-1800 - 39)) {
            return "(JCB GAC without return 9F4B)";
        } else if (errorCode == (-1800 - 40)) {
            return "(JCB GAC without return AC)";
        } else if (errorCode == (-1800 - 41)) {
            return "(JCB GAC without return 9F50)";
        } else if (errorCode == (-1800 - 42)) {
            return "(JCB GAC return AC type wrong)";
        } else if (errorCode == (-1800 - 43)) {
            return "(JCB GAC return AAC)";
        } else if (errorCode == (-1800 - 44)) {
            return "(JCB GAC returns 9F5F format error)";
        } else if (errorCode == (-1800 - 45)) {
            return "(JCB GAC returns 9F60 format error)";
        } else if (errorCode == (-1800 - 46)) {
            return "(JCB does not support CVM authentication)";
        } else if (errorCode == (-1800 - 47)) {
            return "(JCB LEGACY mode GAC returns not 80 template)";
        } else if (errorCode == (-1800 - 48)) {
            return "(JCB LEGACY mode GAC returned CID is not ARQC)";
        } else if (errorCode == (-1800 - 49)) {
            return "(GAC cancels the transaction)";
        } else if (errorCode == (-1800 - 50)) {
            return "(9F27 length error)";
        } else if (errorCode == (-1800 - 51)) {
            return "(9F36 length error)";
        } else if (errorCode == (-1800 - 52)) {
            return "(9F26 length error)";
        } else if (errorCode == (-1800 - 60)) {
            return "(JCB GMD command benchmark Error Code)";
        } else if (errorCode == (-1800 - 60 - 1)) {
            return "(JCB GMD failed to package MDOL)";
        } else if (errorCode == (-1800 - 60 - 2)) {
            return "(JCB GMD command returns data error)";
        } else if (errorCode == (-1800 - 60 - 3)) {
            return "(JCB GMD command without return tag57)";
        } else if (errorCode == (-1800 - 60 - 4)) {
            return "(JCB MS mode GMD return code is 6300)";
        } else if (errorCode == (-1800 - 70)) {
            return "(echo command benchmark Error Code)";
        } else if (errorCode == (-1800 - 70 - 1)) {
            return "(JCB echo command returns data format error)";
        } else if (errorCode == (-1800 - 70 - 2)) {
            return "(JCB echo returns data with duplicate tag)";
        } else if (errorCode == (-1800 - 70 - 3)) {
            return "(JCB echo without return CID)";
        } else if (errorCode == (-1800 - 70 - 4)) {
            return "(JCB echo without return ATC)";
        } else if (errorCode == (-1800 - 70 - 5)) {
            return "(JCB echo without return 9F4B)";
        } else if (errorCode == (-1800 - 70 - 6)) {
            return "(JCB echo without return AC)";
        } else if (errorCode == (-1800 - 70 - 7)) {
            return "(JCB echo without return 9F50)";
        } else if (errorCode == (-1800 - 70 - 8)) {
            return "(Echo command cancel transaction";
        } else if (errorCode == (-1900)) {
            return "(Basic and EMV complete benchmark error code)";
        } else if (errorCode == (-1900 - 1)) {
            return "(Script exceeds limit)";
        } else if (errorCode == (-1900 - 2)) {
            return "(EC script empty)";
        } else if (errorCode == (-1900 - 3)) {
            return "(EC deposit amount exceeds the limit)";
        } else if (errorCode == (-1900 - 4)) {
            return "(Script execution error)";
        } else if (errorCode == (-1900 - 5)) {
            return "(Script error)";
        } else if (errorCode == (-2000)) {
            return "(Qpboc flash card benchmark error code)";
        } else if (errorCode == (-2000 - 1)) {
            return "(Flash card number does not match)";
        } else if (errorCode == (-2000 - 2)) {
            return "(Flash card application transaction counter does not match)";
        } else if (errorCode == (-2000 - 3)) {
            return "(Flash card currency code does not match)";
        } else if (errorCode == (-2000 - 4)) {
            return "(Flash card electronic cash balance does not match)";
        } else if (errorCode == (-2000 - 5)) {
            return "(Flash card without occor transaction)";
        } else if (errorCode == (-2000 - 6)) {
            return "(Flash card GPO error)";
        } else if (errorCode == (-2000 - 7)) {
            return "(The last read record of the flash card is not responding)";
        } else if (errorCode == (-2000 - 8)) {
            return "(Flash card can not get the card number)";
        } else if (errorCode == (-2000 - 9)) {
            return "(Flash card number is different)";
        } else if (errorCode == (-2000 - 10)) {
            return "(Flash card aid is not the same)";
        } else if (errorCode == (-2000 - 11)) {
            return "(The last record of the flash card is not responding.)";
        } else if (errorCode == (-2100)) {
            return "(Preprocessing benchmark error code)";
        } else if (errorCode == (-2100 - 1)) {
            return "(Preprocessing parameter file error)";
        } else if (errorCode == (-2100 - 2)) {
            return "(Preprocessing input amount user exits)";
        } else if (errorCode == (-2100 - 3)) {
            return "(Preprocessing input amount timeout)";
        } else if (errorCode == (-2100 - 4)) {
            return "(Preprocessing input amount failed)";
        } else if (errorCode == (-2100 - 5)) {
            return "(Preprocessed input amount exceeds the limit)";
        } else if (errorCode == (-2100 - 6)) {
            return "(Preprocessing requires online, terminal cannot be online)";
        } else if (errorCode == (-2100 - 7)) {
            return "(AID is 0)";
        } else if (errorCode == (-2100 - 11)) {
            return "(RF card removal failed)";
        } else if (errorCode == (-2100 - 12)) {
            return "(Card returned error)";
        } else if (errorCode == (-2100 - 13)) {
            return "(Failed to read application data)";
        } else if (errorCode == (-2100 - 14)) {
            return "(Card blacklist)";
        } else if (errorCode == (-2100 - 15)) {
            return "(The card is not valid)";
        } else if (errorCode == (-2100 - 16)) {
            return "(Card has invalid)";
        } else if (errorCode == (-2100 - 17)) {
            return "(Card data authentication failed)";
        } else if (errorCode == (-2100 - 18)) {
            return "(Card second magnetic equivalent data failed)";
        } else if (errorCode == (-2100 - 19)) {
            return "(Frequency check exceeds limit)";
        } else if (errorCode == (-2100 - 20)) {
            return "(Pure electronic cash card can not be online)";
        } else if (errorCode == (-2100 - 21)) {
            return "(Card rejection)";
        } else if (errorCode == (-2100 - 22)) {
            return "(Card AIP has no data authentication)";
        } else if (errorCode == (-2100 - 23)) {
            return "(Card 9F10 return transaction result error)";
        } else if (errorCode == (-2100 - 24)) {
            return "(Card data authentication without card number information)";
        } else if (errorCode == (-2100 - 25)) {
            return "(FDDA failed, card and terminal support contact PBOC)";
        } else if (errorCode == (-2100 - 26)) {
            return "(ODA failed, terminal refused to trade)";
        } else if (errorCode == (-2200)) {
            return "(Paypass benchmark error code)";
        } else if (errorCode == (-2200 - 1)) {
            return "(transaction usAmount over all Terminal Contactless Transaction Limit)";
        } else if (errorCode == (-2200 - 2)) {
            return "(paypass error track data)";
        } else if (errorCode == (-2200 - 3)) {
            return "(paypass pcvc3)";
        } else if (errorCode == (-2200 - 4)) {
            return "paypass punatc";
        } else if (errorCode == (-2200 - 5)) {
            return "paypass natc";
        } else if (errorCode == (-2200 - 6)) {
            return "k_track < t_track";
        } else if (errorCode == (-2200 - 7)) {
            return "(track data wrong unpredictable number)";
        } else if (errorCode == (-2200 - 8)) {
            return "(not the same as track2)";
        } else if (errorCode == (-2200 - 9)) {
            return "(not the same as track2)";
        } else if (errorCode == (-2200 - 10)) {
            return "(compute cryptographic checksum error)";
        } else if (errorCode == (-2200 - 11)) {
            return "(compute cryptographic checksum response error)";
        } else if (errorCode == (-2200 - 12)) {
            return "(UDOL NO 9F6A)";
        } else if (errorCode == (-2200 - 13)) {
            return "(CCC command return track2 cvc3 error)";
        } else if (errorCode == (-2200 - 14)) {
            return "(CCC command return track1 cvc3 error)";
        } else if (errorCode == (-2200 - 15)) {
            return "(CCC command return atc error)";
        } else if (errorCode == (-2200 - 16)) {
            return "(NO default UDOL)";
        } else if (errorCode == (-2200 - 17)) {
            return "(UDOL pack error)";
        } else if (errorCode == (-2200 - 18)) {
            return "(input pin error)";
        } else if (errorCode == (-2200 - 19)) {
            return "(read app  error)";
        } else if (errorCode == (-2200 - 20)) {
            return "(Preprocessing input amount user exits)";
        } else if (errorCode == (-2200 - 21)) {
            return "(Preprocessing input amount timeout)";
        } else if (errorCode == (-2200 - 22)) {
            return "(Preprocessing input amount failed)";
        } else if (errorCode == (-2200 - 23)) {
            return "(SDA failed transaction terminate)";
        } else if (errorCode == (-2200 - 24)) {
            return "(Track1 length error)";
        } else if (errorCode == (-2200 - 25)) {
            return "(Track2 length error)";
        } else if (errorCode == (-2200 - 26)) {
            return "(save data error)";
        } else if (errorCode == (-2200 - 27)) {
            return "(read param config error)";
        } else if (errorCode == (-2200 - 28)) {
            return "(CCC command return PCII error)";
        } else if (errorCode == (-2200 - 29)) {
            return "(Used to specifically indicate data loss errors)";
        } else if (errorCode == (-2300)) {
            return "(Paywave benchmark error code)";
        } else if (errorCode == (-2300 - 5)) {
            return "(error or no 9F26 in read record command)";
        } else if (errorCode == (-2300 - 6)) {
            return "(error or no 9F36 in read record command)";
        } else if (errorCode == (-2300 - 7)) {
            return "(error or no 9F10 in read record command";
        } else if (errorCode == (-2300 - 8)) {
            return "(error or no 57 in read record command)";
        } else if (errorCode == (-2300 - 9)) {
            return "(error or no 9F27 in read record command)";
        } else if (errorCode == (-2300 - 10)) {
            return "(international transaction)";
        } else if (errorCode == (-2300 - 11)) {
            return "(Did not return 9F74)";
        } else if (errorCode == (-2300 - 18)) {
            return "(define request online error)";
        } else if (errorCode == (-2300 - 19)) {
            return "(aip no support fdda)";
        } else if (errorCode == (-2400)) {
            return "(Rupay benchmark error code)";
        } else if (errorCode == (-2400 - 1)) {
            return "(Rupay KCV error)";
        } else if (errorCode == (-2500)) {
            return "(MIR benchmark error code)";
        } else if (errorCode == (-2500 - 1)) {
            return "(MIR protocol value error)";
        } else if (errorCode == (-2500 - 2)) {
            return "(MIR aip does not support EMV mode)";
        } else if (errorCode == (-2500 - 3)) {
            return "(MIR Protocol 2 does not have DF6F ODOL)";
        } else if (errorCode == (-2500 - 4)) {
            return "(MIR Protocol 2 DF6F ODOL error)";
        } else if (errorCode == (-2500 - 5)) {
            return "(MIR protocol 2 service not allowed)";
        } else if (errorCode == (-2500 - 6)) {
            return "(MIR without SDAD)";
        } else if (errorCode == (-2500 - 7)) {
            return "(MIR executes transaction commands without returning 9000)";
        } else if (errorCode == (-2500 - 8)) {
            return "(MIR executes transaction commands without returning 77 templates)";
        } else if (errorCode == (-2500 - 9)) {
            return "(MIR executes transaction commands without returning 9F27)";
        } else if (errorCode == (-2500 - 10)) {
            return "(MIR executes transaction commands without returning 9F36)";
        } else if (errorCode == (-2500 - 11)) {
            return "(MIR executes transaction commands without returning 9F71)";
        } else if (errorCode == (-2500 - 12)) {
            return "(MIR executes transaction commands without returning 9000)";
        } else if (errorCode == (-2500 - 13)) {
            return "(MIR executes transaction commands without returning 9F26)";
        } else if (errorCode == (-2500 - 14)) {
            return "(MIR does not support recovery when executing transaction commands)";
        } else if (errorCode == (-2500 - 15)) {
            return "(MIR Executes Transaction Command Restoration Over Restriction)";
        } else if (errorCode == (-2500 - 16)) {
            return "(MIR Executes Transaction Completion Command does not support recovery)";
        } else if (errorCode == (-2500 - 17)) {
            return "(MIR Executes Transaction Completion Command Restore Over Restriction)";
        } else if (errorCode == (-2500 - 18)) {
            return "(MIR Read Record Command does not support recovery)";
        } else if (errorCode == (-2500 - 19)) {
            return "(MIR Read Record Command Restore Over Restriction)";
        } else if (errorCode == (-4000)) {
            return "(Public key file operation benchmark error code)";
        } else if (errorCode == (-4000 - 1)) {
            return "(File open error)";
        } else if (errorCode == (-4000 - 2)) {
            return "(write file error)";
        } else if (errorCode == (-4000 - 3)) {
            return "(Read file error)";
        } else if (errorCode == (-4000 - 4)) {
            return "(Public key checksum error)";
        } else if (errorCode == (-4000 - 5)) {
            return "(This public key was not found)";
        } else if (errorCode == (-4000 - 6)) {
            return "(Parameter error)";
        } else if (errorCode == (-4000 - 7)) {
            return "(File length error)";
        } else if (errorCode == (-4100)) {
            return "(Public key collection list and card blacklist file operation benchmark error code)";
        } else if (errorCode == (-4100 - 1)) {
            return "(File open error)";
        } else if (errorCode == (-4100 - 2)) {
            return "(write file error)";
        } else if (errorCode == (-4100 - 3)) {
            return "(Read file error)";
        } else if (errorCode == (-4100 - 4)) {
            return "(Data length is exceeds limit)";
        } else if (errorCode == (-4100 - 5)) {
            return "(Without find the corresponding record)";
        } else if (errorCode == (-4100 - 6)) {
            return "(Parameter error)";
        } else if (errorCode == (-4100 - 7)) {
            return "file length error";
        } else if (errorCode == (-5000)) {
            return "(AID parameter file operation benchmark error code)";
        } else if (errorCode == (-5000 - 1)) {
            return "(File open error)";
        } else if (errorCode == (-5000 - 2)) {
            return "(write file error)";
        } else if (errorCode == (-5000 - 3)) {
            return "(Read file error)";
        } else if (errorCode == (-5000 - 4)) {
            return "(Public key checksum error)";
        } else if (errorCode == (-5000 - 5)) {
            return "(This AID was not found)";
        } else if (errorCode == (-5000 - 6)) {
            return "(Parameter error)";
        } else if (errorCode == (-5000 - 7)) {
            return "(File length error)";
        } else if (errorCode == (-5000 - 8)) {
            return "(Synchronous update of AID corresponding data fails when updating terminal configuration parameters)";
        } else if (errorCode == (-5000 - 9)) {
            return "(AID parsing failed)";
        } else if (errorCode == (-5000 - 10)) {
            return "(AID is not available)";
        } else return "UNKNOW";
    }

}


