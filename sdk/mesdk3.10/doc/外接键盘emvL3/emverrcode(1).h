#ifndef __EMV_ERRCODE_H__
#define __EMV_ERRCODE_H__


//BASE
#define EMV_ERR_BASE							(0)		/*(EMV benchmark error code)*/
#define EMV_ERR_READCONFIG						(EMV_ERR_BASE - 1)        /*(Failed to read aid configuration)*/
#define EMV_ERR_READAIDLIST						(EMV_ERR_BASE - 2)        /*(Failed to read aid list)*/
#define EMV_ERR_POWERUP							(EMV_ERR_BASE - 3)        /*(IC card unable to power on)*/
#define EMV_ERR_NOTSUPPORT						(EMV_ERR_BASE - 4)        /*(IC card not support instruction)*/
#define EMV_ERR_APPBLOCK						(EMV_ERR_BASE - 5)        /*(Application lock)*/
#define EMV_ERR_FINDAPP							(EMV_ERR_BASE - 6)        /*(Can't find supported applications)*/
#define EMV_ERR_Cancel							(EMV_ERR_BASE - 7)        /*(Quit the transaction)*/
#define EMV_ERR_SELECTAPP						(EMV_ERR_BASE - 8)        /*(Application selection failed)*/
#define EMV_ERR_APPINIT							(EMV_ERR_BASE - 9)        /*(Application initialization failed)*/
#define EMV_ERR_READAPPDATA						(EMV_ERR_BASE - 10)       /*(Failed to read application data)*/
#define EMV_ERR_OFFAUTH							(EMV_ERR_BASE - 11)       /*(Offline data authentication failed)*/
#define EMV_ERR_PROCESSLIMIT					(EMV_ERR_BASE - 12)       /*(Process limit failed)*/
#define EMV_ERR_CARDVERIFY						(EMV_ERR_BASE - 13)       /*(Cardholder authentication failed)*/
#define EMV_ERR_TERMRISKMANAGE					(EMV_ERR_BASE - 14)       /*(Terminal risk management failed)*/
#define EMV_ERR_TERMACTANALYZE					(EMV_ERR_BASE - 15)       /*(Terminal behavior analysis failed)*/
#define EMV_ERR_NOTSUPPORTSERVICE				(EMV_ERR_BASE - 16)       /*(Unsupported service)*/
#define EMV_ERR_NORANDNUM						(EMV_ERR_BASE - 17)       /*(No random number)*/
#define EMV_ERR_CARDBLOCK						(EMV_ERR_BASE - 18)       /*(Card lock)*/
#define EMV_ERR_COMPLETION						(EMV_ERR_BASE - 19)       /*(GEN AC execution failed*/
#define EMV_ERR_SAVECONFIG						(EMV_ERR_BASE - 20)       /*(Save key configuration failed)*/
#define EMV_ERR_RF_PREPROCESS					(EMV_ERR_BASE - 30)       /*(RF card preprocessing failed)*/
#define EMV_ERR_TIMEOUT							(EMV_ERR_BASE - 31)       /*(Operation timeout)*/
#define EMV_ERR_RF_GETICC						(EMV_ERR_BASE - 32)       /*(Inserted IC card detected during RF card finder)*/
#define EMV_ERR_AID_COUNT_EXCEED				(EMV_ERR_BASE - 33)		  /*(Too many AIDs)*/
#define EMV_ERR_AID_EMPTY						(EMV_ERR_BASE - 34)		  /* empty aid*/ 
#define EMV_ERR_L3_START						(EMV_ERR_BASE-500)		  /*reserve for L3-start */
#define EMV_ERR_L3_END							(EMV_ERR_BASE-800)		  /*reserve for L3-end */

//File Process
#define FILEERR_BASE							(EMV_ERR_BASE - 900)		/*(AID configuration file benchmark error code)*/
#define FILE_OPEN_FILE							(FILEERR_BASE - 1)		/*(AID configuration file open failed)*/
#define FILE_READ_FILE							(FILEERR_BASE - 2)		/*(AID configuration file read failed)*/
#define FILE_WRITE_FILE							(FILEERR_BASE - 3)		/*(AID configuration file write failed)*/
#define FILE_AID_VERSION						(FILEERR_BASE - 4)		/*(AID configuration file version error)*/
#define FILE_GETTLVDATA_NOEXIST					(FILEERR_BASE - 5)		/*(AID configuration file Can't get the required AID)*/
#define FILE_AIDERR_PARSE						(FILEERR_BASE - 6)		/*(AID configuration file Tlv data parsing failed)*/
#define FILE_AIDERR_API							(FILEERR_BASE - 7)		/*(Use wrong API interface)*/


//ICC Apdu
#define ICCERR_BASE								(EMV_ERR_BASE - 1000)	/*(ICC Apdu benchmark error code)*/
#define APDU_DATA_NULL							(ICCERR_BASE - 1)		/*(apdu interactive data is empty*/

#define COREERR_BASE							(EMV_ERR_BASE - 1100)	/*(Kernel basic operation benchmark error code*/
#define COREERR_GETTIME							(COREERR_BASE - 1)      /**< (Get POS time error) */
#define COREERR_READFINALPARAM					(COREERR_BASE - 2)      /**<  (READ FINAL PARAM error)*/
#define COREERR_GETUNPNUM						(COREERR_BASE - 3)      /**<  (Get random number error)*/

#define BUFERR_BASE								(EMV_ERR_BASE - 1200)	/*(Data cache benchmark error code)*/
#define BUFERR_BUFOVER          				(BUFERR_BASE - 1)       /* (Data buffer not enough storage)*/
#define BUFERR_OBJDUP           				(BUFERR_BASE - 2)       /* (The label is unique and cannot be overwritten if the length is greater than zero)*/

//Application Select
#define SELERR_BASE								(EMV_ERR_BASE - 1300)	/*(Application select benchmark error code)*/
#define SEL_USE_AIDLIST							(SELERR_BASE - 1)		/*(application select AID list failed)*/
#define SEL_QUIT								(SELERR_BASE - 2)		/*(Application select cancle the transaction)*/
#define SEL_FCIFMTERR							(SELERR_BASE - 3)		/*(FCI data format error)*/
#define SEL_FCINO6F								(SELERR_BASE - 4)		/*(FCI data without 6F)*/
#define SEL_FCINO84								(SELERR_BASE - 5)		/*(FCI data without 84)*/
#define SEL_FCINOA5								(SELERR_BASE - 6)		/*(FCI data without A5)*/
#define SEL_POSERR9F38							(SELERR_BASE - 7)		/*(FCI data error 9F38)*/
#define SEL_BF0CDUP								(SELERR_BASE - 8)		/*(FCI data tagBF0C repeat)*/
#define SEL_FALLBACK							(SELERR_BASE - 9)		/*(Application selection return transaction)*/
#define SEL_FCI50DUP							(SELERR_BASE - 10)		/*(FCI data tag50 repeat)*/
#define SEL_FCIDUP								(SELERR_BASE - 11)		/*(FCI data repeat)*/
#define SEL_TLV_ERR								(SELERR_BASE - 12)		/*(FCI data TLV parsing failed)*/
#define SEL_NO6F								(SELERR_BASE - 13)		/*(FCI data without tag 6F)*/
#define SEL_NO84								(SELERR_BASE - 14)		/*(FCI data without tag 84)*/
#define SEL_NOA5								(SELERR_BASE - 15)		/*(FCI data without tag A5)*/
#define SEL_TAG_SEQERR							(SELERR_BASE - 16)      	  /* (The tag sequence is wrong. For example"tag '84' should be placed before tag 'A5'"*/
#define SEL_NOBF0C								(SELERR_BASE - 17)		/*(FCI data without tag BF0C)*/
#define SEL_BFOC_DATAERR						(SELERR_BASE - 18)		/*(FCI data tag 8F0C parsing failed)*/
#define SEL_AID_DIFF_DFNAME						(SELERR_BASE - 19)        /*(final selection, return DF Name  not the same as the AID of command)*/
#define SEL_ERR_TRANSTYPE						(SELERR_BASE - 20)        /*(Wrong transaction type)*/
#define SEL_PPSE_ERROR							(SELERR_BASE - 21)        /*(PPSE command returned failure)*/
#define SEL_NEXT_AID							(SELERR_BASE - 22)       /*(Select the next AID)*/
#define SEL_DISCOVER_ZIP_AID					(SELERR_BASE - 23)		/*(Discover zip aid special treatment for ppse)*/
#define SEL_NO84VALUE							(SELERR_BASE - 24)		/*(FCI data without the value of tag 84)*/
#define SEL_DPASNOHAVEZIPAID					(SELERR_BASE - 25)		/*(DPAS ppse only returns dpas aid does not return zip aid)*/
#define SEL_AMT_OVER_CLSS						(SELERR_BASE - 27)		/*(contactless transaction amount  exceeds limit)*/
#define SEL_AMTZERO_CLSS_NOT_ALLOW				(SELERR_BASE - 28)	/*(when Amount zero, Set the Contactless Application Not Allowed)*/
#define SEL_AMTZERO_CLSS_OFFLINE_OLY 			(SELERR_BASE - 29)		/*£¨when Amount zero, shall set go online, but terminal offline only£©*/

#define SEL_TAG61_INVALID						(SELERR_BASE - 35)		/*£¨Application selection tag61 is invalid£©*/
#define SEL_TAG4F_INVALID						(SELERR_BASE - 36)		/*£¨Application selection tag4F is invalid)*/
#define SEL_TAG4F_PARTIAL						(SELERR_BASE - 37)		/*(Application selection tag4F partial matching incomplete)*/

#define SEL_FCI_FMTERR							(SELERR_BASE - 38)		/*(JCB FCI format error)*/
#define SEL_FCIERR84							(SELERR_BASE - 39)		/*(JCB FCI 84 error)*/
#define SEL_FCINO50								(SELERR_BASE - 40)		/*(FCI without 50,)*/
#define SEL_FCINO9F38							(SELERR_BASE - 41)		/*(JCB FCI without 9F38)*/
#define SEL_FCIEMPTY9F28						(SELERR_BASE - 42)		/*(JCB FCI 9F28 is empty)*/
#define SEL_FCI_ILLEGALTAG						(SELERR_BASE - 43)		/*(JCB FCI failed to find proprietary data)*/
#define SEL_FCI_REV_LEGACY						(SELERR_BASE - 44)		/*(JCB Torn transaction is Legacy Mode)*/
#define SEL_FCI_MANDATAMISS						(SELERR_BASE - 45)		/*(MCCS enforces data loss)*/

#define SEL_FINALSEL_6300						(SELERR_BASE - 50)		/*(Application selection returns 6300,State of non-volatile memory changed; authentication failed)*/
#define SEL_FINALSEL_63C1						(SELERR_BASE - 51)		/*(Application selection returns 63C1)*/
#define SEL_FINALSEL_6983						(SELERR_BASE - 52)		/*(Application selection returns 6983,Command not allowed; authentication method blocked)*/
#define SEL_FINALSEL_6984						(SELERR_BASE - 53)		/*(Application selection returns 6984,Command not allowed; reference data not usable)*/
#define SEL_FINALSEL_6985						(SELERR_BASE - 54)		/*(Application selection returns 6985,Command not allowed; conditions of use not satisfied)*/
#define SEL_FINALSEL_6A82						(SELERR_BASE - 55)		/*(Application selection returns 6A82,Wrong parameters P1-P2; file or application not found)*/
#define SEL_FINALSEL_6A83						(SELERR_BASE - 56)		/*(Application selection returns 6A83,Wrong parameters P1-P2; record not found)*/
#define SEL_FINALSEL_6A88						(SELERR_BASE - 57)		/*(Application selection returns 6A88,Reference data (data objects) not found)*/
#define SEL_FINALSEL_6400						(SELERR_BASE - 58)		/*(Application selection returns 6400)*/
#define SEL_FINALSEL_6500						(SELERR_BASE - 59)		/*(Application selection returns 6500)*/
#define SEL_FINALSEL_9001						(SELERR_BASE - 60)		/*(Application selection returns 9001)*/
#define SEL_L1_FAIL								(SELERR_BASE - 61)		/*(Application selection apdu communication failed)*/

/*Select FCI Response Analysis Error Code*/
#define KA_ERR_BASE						(SELERR_BASE - 70)	/*(JCB benchmark error code)*/
#define KA_CONFIG_EMPTY					(KA_ERR_BASE - 1)		/*(JCB configured to be empty)*/
#define KA_LEGACY_NOTSUPPORTED			(KA_ERR_BASE - 2)		/*(JCB Legacy mode is not supported)*/

//Application Initialization
#define INITERR_BASE							(EMV_ERR_BASE - 1400)	/*(Application initialization benchmark error code)*/
#define INITERR_DOLPACKET						(INITERR_BASE - 1)	/*(Application initialization PDOL packaging failed)*/
#define INITERR_RETURNSEL						(INITERR_BASE - 2)	/*(Application initialization return value is not equal to 9000)*/
#define INITERR_GPOCMD							(INITERR_BASE - 3)	/*(Application initialization returns 6984)*/
#define INITERR_TLVDECODE						(INITERR_BASE - 4)	/*(Application initialization returns TLV parsing error)*/
#define INITERR_80VALUELEN						(INITERR_BASE - 5)	/*(Application initialization returns 80 template length error)*/
#define INITERR_77NOAIP							(INITERR_BASE - 6)	/*(Application initialization returns 77 template without AIP)*/
#define INITERR_AIPLEN							(INITERR_BASE - 7)	/*(Application initialization returns AIP length error)*/
#define INITERR_77NOAFL							(INITERR_BASE - 8)	/*(Application initialization returns 77 template without AFL)*/
#define INITERR_AFLLEN							(INITERR_BASE - 9)	/*(Application initialization returns AFL length error)*/
#define INITERR_UNEXPECTTAG						(INITERR_BASE - 10)	/*(Application initialization returns invalid tag)*/
#define INITERR_NOPDOL							(INITERR_BASE - 11)	/*(Application initialization without PDOL)*/
#define INITERR_RETURNDATA						(INITERR_BASE - 12)	/*(Application initialize pboc return 80 template)*/
#define INITERR_CARDNOSUPPORT					(INITERR_BASE - 13)	/*(Application initialization card is not supported)*/
#define INITERR_ECSELECT_QUIT					(INITERR_BASE - 14)	/*(Cancel electronic cash transactions)*/
#define INITERR_ECONLY_DENIAL					(INITERR_BASE - 15)	/*(Pure electronic cash card, but does not support e-cash)*/
#define INITERR_GPO_RETURN_6984					(INITERR_BASE - 16) 	/*(Application initialization returns 6984< 20120911 zhengel 6984 special treatment)*/
#define INITERR_GPO_RETURN_6985					(INITERR_BASE - 17) 	/*(Application initialization returns 6985 <20160330 fangjt 6985 directly terminate the transaction)*/
#define INITERR_GPO_RETURN_6283					(INITERR_BASE - 18)	/*(Application initialization returns 6283)*/
#define INITERR_GPO_RETURN_6300					(INITERR_BASE - 19)	/*(Application initialization returns 6300)*/
#define INITERR_GPO_RETURN_63C1					(INITERR_BASE - 20)	/*(Application initialization returns 63C1)*/
#define INITERR_GPO_RETURN_6983					(INITERR_BASE - 21)	/*(Application initialization returns 6983)*/
#define INITERR_GPO_RETURN_6986					(INITERR_BASE - 22)	/*(Application initialization returns 6986)*/
#define INITERR_GPO_RETURN_9001					(INITERR_BASE - 23)	/*(Application initialization returns 9001)*/
#define INITERR_GPO_RETURN_6A81					(INITERR_BASE - 24)	/*(Application initialization returns 6A81)*/
#define INITERR_GPO_RETURN_6A82					(INITERR_BASE - 25)	/*(Application initialization returns 6A82)*/
#define INITERR_GPO_RETURN_6A83					(INITERR_BASE - 26)	/*(Application initialization returns 6A83)*/
#define INITERR_GPO_RETURN_6A88					(INITERR_BASE - 27)	/*(Application initialization returns 6A88)*/
#define INITERR_GPO_RETURN_6500					(INITERR_BASE - 28)	/*(Application initialization returns 6500)*/
#define INITERR_GPO_RETURN_6400					(INITERR_BASE - 29)	/*(Application initialization returns 6400)*/
#define INITERR_GPO_RETURN_9408					(INITERR_BASE - 30)	/*(Application initialization returns 9408)*/
#define INITERR_RF_ATC							(INITERR_BASE - 31)	/*(Application initialization returns ATC error)*/
#define INITERR_RF_AC							(INITERR_BASE - 32)	/*(Application initialization returns AC error)*/
#define INITERR_RF_9F10							(INITERR_BASE - 33)	/*(Application initialization returns 9F10 error)*/
#define INITERR_RF_57							(INITERR_BASE - 34)	/*(Application initialization returns 57 error)*/
#define INITERR_RF_AFL							(INITERR_BASE - 35)	/*(Application initialization returns AFL error)*/
#define INITERR_RF_5F20							(INITERR_BASE - 36)	/*(Application initialization returns 5F20 error)*/
#define INITERR_RF_NO9F66						(INITERR_BASE - 37)	/*(Application initialization without 9F66)*/
#define INITERR_RF_INSERTICC					(INITERR_BASE - 38)	/*(The insert card is detected during the contactless application initialization process)*/
#define INITERR_RF_9F27							(INITERR_BASE - 39)	/*(Application initialization 9F27 error)*/
#define INITERR_RF_APPNOSUPPORT					(INITERR_BASE - 40)	/*(Application initialization APP does not support)*/
#define INITERR_RF_ECONLY_ONLINE				(INITERR_BASE - 41)	/*(Pure electronic cash card but requires online)*/
#define INITERR_RF_ECONLY_CVM					(INITERR_BASE - 42)	/*(Pure electronic cash card but requires CVM)*/
#define INITERR_RF_STRIPE						(INITERR_BASE - 43)	/*(Magnetic stripe card is detected during the contactless application initialization process.)*/
#define INITERR_SELECT_KERNEL_ERR				(INITERR_BASE - 44)	/*(Application initialization returns ATC error)*/
#define INITERR_GPO_SAVEDATAERR					(INITERR_BASE - 45)	/*(Application initialization failed to save data)*/
#define INITERR_77DUB_AFL						(INITERR_BASE - 46)	/*(Application initialization returns 77 template AFL data duplication)*/
#define INITERR_80DUB_AFL						(INITERR_BASE - 47)	/*(Application initialization returns 80 template AFL data duplication)*/
#define INITERR_81_GET							(INITERR_BASE - 48)	/*(Application initialization gets 81)*/
#define INITERR_77DUB_AIP						(INITERR_BASE - 49)	/*(Application initialization returns 77 template AIP data duplication)*/
#define INITERR_80DUB_AIP						(INITERR_BASE - 50)	/*(Application initialization returns 80 template AIP data duplication)*/
#define INITERR_9F01_GET						(INITERR_BASE - 51)	/*(Application initialization returns 9F01 error)*/
#define INITERR_9F69_GET						(INITERR_BASE - 52)	/*(Application initialization returns 9F69 error)*/
#define INITERR_SAVE_FAIL						(INITERR_BASE - 53)	/*(Application initialization failed to save data)*/
#define INITERR_PARSE_ERR						(INITERR_BASE - 54)	/*(Application initialization tlv data parsing error)*/
#define INITERR_CARDDATA_MISSING				(INITERR_BASE - 55)	/*(Application initialization card data lose)*/
#define INITERR_AFLHEADERR						(INITERR_BASE - 56)	/*(Application initialization AFL format error)*/
#define INITERR_DUPLICATED						(INITERR_BASE - 57)	/*(Application initialization tag repeat)*/
#define INITERR_AIP_MISSING						(INITERR_BASE - 58)	/*(JCB application initialization without return AIP)*/
#define INITERR_SFI_MISSING						(INITERR_BASE - 59)	/*(JCB application initialization without return SFI)*/
#define INITERR_ILLEGALTAG						(INITERR_BASE - 60)	/*(JCB application initialization is not 77 or 80 template)*/
#define INITERR_INVALID_SFI						(INITERR_BASE - 61)	/*(JCB application initializes SFI error)*/
#define INITERR_QUIT							(INITERR_BASE - 62)	/*(Application initialization cancel transaction)*/
#define INITERR_FFI_NOSUPP_CONTAACTLESS			(INITERR_BASE - 63)	/*(Interac FFI does not support contactless)*/
#define INITERR_FFI_NOSUPP_MOBILE				(INITERR_BASE - 64)	/*(Interac FFI does not support mobile phones)*/
#define INITERR_FFI_WRONG						(INITERR_BASE - 65)	/*(Interac FFI value error)*/
#define INITERR_NO_CTI							(INITERR_BASE - 66)	/*£¨interac no CTI£©*/
#define INITERR_CTI_LENTHWRONG					(INITERR_BASE - 67)	/*£¨interac CTI lenth wrong£©*/
#define INITERR_CTI_TRYOTHERINTERFACE			(INITERR_BASE - 68)	/*£¨Interac CTI requires a transfer interface£©*/
#define INITERR_CTI_OTHERTERMINAL				(INITERR_BASE - 69)	/*£¨Interac CTI requires a transfer interface, but this machine does not support,viewing other machines£©*/
#define INITERR_CTI_NOOTHERTERMINAL				(INITERR_BASE - 70)	/*£¨Interac CTI requires a transfer interface, which is not supported by this machine and other machines.£©*/
#define INITERR_OVER_RETRYLIMIT					(INITERR_BASE - 71)	/*£¨Interac exceeds the maximum number of try again£©*/
#define INITERR_SECONDTAPWRONG					(INITERR_BASE - 72)	/*£¨Rupay second Remove the card and put it back later is mismatch£©*/
#define INITERR_GPO_RESPONSE_ERR				(INITERR_BASE - 72)	/*£¨GPO APDU response exception£©*/


//Read Application Data
#define READRECERR_BASE							(EMV_ERR_BASE - 1500)	/*£¨Read record benchmark error code£©*/
#define READREC_SFIERR							(READRECERR_BASE - 1)   		/*£¨Read record SFI error£©*/
#define READREC_FIRSTBE0						(READRECERR_BASE - 2)   		/*£¨Read record first record error£©*/
#define READREC_RECRANGEERR						(READRECERR_BASE - 3)  	 /*£¨Read record SFI record range error£©*/
#define READREC_CMDERR							(READRECERR_BASE - 4)  		 /*£¨Read record return code error£©*/
#define READREC_TLVERR							(READRECERR_BASE - 5) 	 	 /*£¨Read record TLV parsing error£©*/
#define READREC_NO5A							(READRECERR_BASE - 6)  		 /*£¨Read record without return tag 5A£©*/
#define READREC_NO8C							(READRECERR_BASE - 7)  		 /* (Read record without return tag 8C)*/
#define READREC_NO8D							(READRECERR_BASE - 8)   		/*(Read record without return tag 8D)*/
#define READREC_5F24ERR							(READRECERR_BASE - 9)   	/*(Read record tag5F24 error)*/
#define READREC_5F25ERR							(READRECERR_BASE - 10)   	/*(Read record tag5F25 error)*/
#define READREC_SAVEDATA						(READRECERR_BASE - 11)  		 /*(Failed to read record storage data)*/
#define READREC_RET_TEMDATA						(READRECERR_BASE - 12)		/*£¨readrecord return Terminal Data,so terminate£©*/
#define READREC_LASTCMDERR						(READRECERR_BASE - 13)		/*(The last readrecord cmd failed (this macro value is immutable))*/
#define READREC_NO57							(READRECERR_BASE - 14) 		  /*(Read record without return tag 57)*/
#define READREC_NO5F20							(READRECERR_BASE - 15) 		  /*(Read record without return tag 5F20)*/
#define READREC_NO9F74							(READRECERR_BASE - 21) 		  /*(Read record without return tag 9F74)*/
#define READREC_NO9F79							(READRECERR_BASE - 22) 		  /*(Read record without return tag 9F79)*/
#define READREC_NO9F36							(READRECERR_BASE - 23)  		 /*(Read record without return tag 9F36)*/
#define READREC_ERR_ATC							(READRECERR_BASE - 24)  		 /*(Read record returns tag 9F36 error)*/
#define READREC_ERR_DATEEXPIRE					(READRECERR_BASE - 31)  		 /*(Transaction date expired)*/
#define READREC_ERR_DATENOEFFECT				(READRECERR_BASE - 32)	  	 /*(The date of transaction is not valid)*/
#define READREC_RETURN_6283						(READRECERR_BASE - 50) 		/*(Read record returns to 6283)*/
#define READREC_RETURN_6300						(READRECERR_BASE - 51) 		/*(Read record returns to 6300)*/
#define READREC_RETURN_63C1						(READRECERR_BASE - 52) 		/*(Read record returns to 63C1)*/
#define READREC_RETURN_6983						(READRECERR_BASE - 53) 		/*(Read record returns to 6983)*/
#define READREC_RETURN_6984						(READRECERR_BASE - 54) 		/*(Read record returns to 6984)*/
#define READREC_RETURN_6985						(READRECERR_BASE - 55) 		/*(Read record returns to 6985)*/
#define READREC_RETURN_6A81						(READRECERR_BASE - 56) 		/*(Read record returns to 6A81)*/
#define READREC_RETURN_6A82						(READRECERR_BASE - 57) 		/*(Read record returns to 6A82)*/
#define READREC_RETURN_6A83						(READRECERR_BASE - 58) 		/*(Read record returns to 6A83)*/
#define READREC_RETURN_6A88						(READRECERR_BASE - 59) 		/*(Read record returns to 6A88)*/
#define READREC_RETURN_6400						(READRECERR_BASE - 60) 		/*(Read record returns to 6400)*/
#define READREC_RETURN_6500						(READRECERR_BASE - 61) 		/*(Read record returns to 6500)*/
#define READREC_RETURN_9001						(READRECERR_BASE - 62) 		/*(Read record returns to 9001)*/
#define READREC_RETURN_5ADUP					(READRECERR_BASE - 63)		/*(Read record returns 5A repeat)*/
#define READREC_RETURN_5F24DUP					(READRECERR_BASE - 64)		/*(Read record returns 5F24 repeat)*/
#define READREC_RETURN_57DUP					(READRECERR_BASE - 65)		/*(Read record returns 57 repeat)*/
#define READREC_QUIT							(READRECERR_BASE - 66)		/*(Read record cancel transaction)*/
#define READREC_DATEWRONG						(READRECERR_BASE - 67)		/*(The date of the card returned by the read record is incorrect)*/
#define READREC_NOSAME5A57							(READRECERR_BASE - 68)		/*(5A and 57 returned by the read record do not match)*/

#define READREC_NO9F08							(READRECERR_BASE - 69)   	/*(Read record without return tag 9F08)*/
#define READREC_NO9F02							(READRECERR_BASE - 70)   	/*(Read record without return tag 9F02)*/
#define READREC_MAX_LIMIT_EXCEEDED				(READRECERR_BASE - 71) 	    /*(Read records exceed the maximum limit)*/
#define READREC_ERR_PP_ERR_9F4A					(READRECERR_BASE - 72)			/*(Read record returns tag 9F4A error)*/
#define READREC_ERR_PP_NO9F4A					(READRECERR_BASE - 73) 	   	 /*(Read record without return tag 9F4A)*/
#define READREC_ERR_PP_NO8F						(READRECERR_BASE - 74) 		   /*(Read record without return tag 8F)*/
#define READREC_ERR_PP_NO90						(READRECERR_BASE - 75)  	  /*(Read record without return tag 90)*/
#define READREC_ERR_PP_NO9F32					(READRECERR_BASE - 76)   	 /*(Read record without return tag 9F32)*/
#define READREC_ERR_PP_NO93						(READRECERR_BASE - 77)  	  /*(Read record without return tag 93)*/
#define READREC_ERR_PP_NO9F46					(READRECERR_BASE - 78)    	/*(Read record without return tag 9F46)*/
#define READREC_ERR_PP_NO9F47					(READRECERR_BASE - 79)   	 /*(Read record without return tag 9F47)*/
#define READREC_ERR_PP_CAPKNOSURPT				(READRECERR_BASE - 80)   	 /* (Capk does not support)*/
#define READREC_ERR_PP_CARDDATA					(READRECERR_BASE - 81)   	 /* £¨ICC return CardReader Data,so terminate£©*/
#define READREC_ERR_PW_NO57						(READRECERR_BASE - 82) 		/*(Read record without return tag 57)*/
#define READREC_FMT_ERROR						(READRECERR_BASE - 83) 		/*£¨Read record return data format error£©*/
#define READREC_NO5F24							(READRECERR_BASE - 84) 		/*(Read record without return tag 5F24)*/
#define READREC_TAG9F42_INVALID					(READRECERR_BASE - 85)		/*£¨Read record TAG 9F42 INVALID*/
#define READREC_TAG5F25_INVALID					(READRECERR_BASE - 86)		/*£¨Read record TAG 5F25 INVALID*/
#define READREC_TAG5A_INVALID					(READRECERR_BASE - 87)		/*£¨Read record TAG 5A INVALID*/
#define READREC_TAG9F07_INVALID					(READRECERR_BASE - 88)		/*£¨Read record TAG 9F07 INVALID*/
#define READREC_TAG5F20_INVALID					(READRECERR_BASE - 89)		/*£¨Read record TAG 5F20 INVALID*/
#define READREC_TAG9F0D_INVALID					(READRECERR_BASE - 90)		/*£¨Read record TAG 9F0D INVALID*/
#define READREC_TAG9F0E_INVALID					(READRECERR_BASE - 91)		/*£¨Read record TAG 9F0E INVALID*/
#define READREC_TAG9F0F_INVALID					(READRECERR_BASE - 92)		/*£¨Read record TAG 9F0F INVALID*/
#define READREC_TAG5F34_INVALID					(READRECERR_BASE - 93)		/*£¨Read record TAG 5F34 INVALID*/
#define READREC_TAG9F11_INVALID					(READRECERR_BASE - 94)		/*£¨Read record TAG 9F11 INVALID*/
#define READREC_TAG5F28_INVALID					(READRECERR_BASE - 95)		/*£¨Read record TAG 5F28 INVALID*/
#define READREC_TAG8F_INVALID					(READRECERR_BASE - 96)		/*£¨Read record TAG 8F INVALID*/
#define READREC_NO9F6D							(READRECERR_BASE - 97) 		  /*(Read record without return tag 9F6D)*/


//Data Authentication
#define SECERR_BASE								(EMV_ERR_BASE - 1600)		/*£¨Data authentication benchmark error code£©*/
#define SECERR_ICCDATAMISSING					(SECERR_BASE - 1)		/*£¨Ic card data loss£©*/
#define SECERR_CERTLENGTH						(SECERR_BASE - 2)		/*£¨Data Authentication Certificate Length Error£©*/
#define SECERR_RECOVERKEY						(SECERR_BASE - 3)		/*£¨Data authentication RSA failed to recover public key£©*/
#define SECERR_DATAHEADER						(SECERR_BASE - 4)		/*£¨Data authentication recovery data header error£©*/
#define SECERR_FORMATWRONG						(SECERR_BASE - 5)		/*£¨The second byte of the data authentication certificate is wrong£©*/
#define SECERR_DATATAILER						(SECERR_BASE - 6)		/*£¨Second to last byte of the data authentication certificate is wrong£©*/
#define SECERR_RECOVERHASH						(SECERR_BASE - 7)		/*£¨Data authentication hash value check does not match£©*/
#define SECERR_ALGORITHM						(SECERR_BASE - 8)		/*£¨Data authentication algorithm identification error£©*/
#define SECERR_MODULUSLENGTH					(SECERR_BASE - 9)		/*£¨Data Authentication Certificate Module Length error£©*/
#define SECERR_CERTEXPIRED						(SECERR_BASE - 10)		/*£¨Data Authentication Certificate expires£©*/
#define SECERR_CERTREVOC						(SECERR_BASE - 11)		/*£¨Data authentication and certification center public key blacklist£©*/
#define SECERR_SSADLEN							(SECERR_BASE - 12)		/*£¨Data authentication SSAD length error£©*/
#define SECERR_RECOVERSSAD						(SECERR_BASE - 13)		/*£¨Data authentication recovery SSAD error£©*/
#define SECERR_9F4AERR							(SECERR_BASE - 14)		/*£¨Data authentication 9F4A error£©*/
#define SECERR_NONEDDOL							(SECERR_BASE - 15)		/*£¨Data authentication without default DDOL£©*/
#define SECERR_NO9F37							(SECERR_BASE - 16)		/*(Data authentication without tag 9F37)*/
#define SECERR_DDOLPROCESS						(SECERR_BASE - 17)		/*(Data authentication DDOL package error)*/
#define SECERR_SDADLEN							(SECERR_BASE - 18)		/*(Data authentication SSAD length error)*/
#define SECERR_NOISSUERPK						(SECERR_BASE - 19)		/*(Data authentication without issuing bank public key)*/
#define SECERR_NOICCPK							(SECERR_BASE - 20)		/*(Data authentication without IC card public key)*/
#define SECERR_GETCHALLENGE						(SECERR_BASE - 21)		/*(Data Authentication Acquisition Random Number error)*/
#define SECERR_RECOVERENCPIN					(SECERR_BASE - 22)		/*(Data Authentication Recovery RSA Data error)*/
#define SECERR_NO9F4B							(SECERR_BASE - 23)		/*(Data authentication without tag 9F4B)*/
#define SECERR_RECOVERSDAD						(SECERR_BASE - 24)		/*(Data Authentication Recovery SDAD Error)*/
#define SECERR_SCDADLEN							(SECERR_BASE - 25)		/*(Data Authentication SCDAD Length Error)*/
#define SECERR_RECOVERSCDAD						(SECERR_BASE - 26)		/*(Data Authentication Recovery SCDAD Error)*/
#define SECERR_CIDNOTMATCHED					(SECERR_BASE - 27)		/*(Data Authentication CID Value Mismatch)*/
#define SECERR_CDAHASH1							(SECERR_BASE - 28)		/*(Data authentication hash check 1 error)*/
#define SECERR_CDAHASH2							(SECERR_BASE - 29)		/*(Data authentication hash check 2 error)*/
#define SECERR_FAILINREADREC					(SECERR_BASE - 30)		/*(Data Authentication Reading Record Error)*/
#define SECERR_PANNOTMATCH						(SECERR_BASE - 31)		/*(Data Authentication Card Number Mismatch)*/
#define SECERR_CAPKNOTFOUND						(SECERR_BASE - 32)		/*(Data Authentication and Authentication Center Public Key Mismatch)*/
#define SECERR_NO9F36							(SECERR_BASE - 33)		/*(Data authentication without tag 9F36)*/
#define SECERR_FDDAVERNOTSUP					(SECERR_BASE - 34)		/*(Data Authentication FDDA Version does not support)*/
#define SECERR_FDDA9F69LENERR					(SECERR_BASE - 35)		/*(Data Authentication FDDA 9F69 Length Error)*/
#define SECERR_ALGORITHMPARAM					(SECERR_BASE - 36)		/*(Data Authentication national secret Elliptic Parameter Identification Error)*/
#define SECERR_SM2VERIFY						(SECERR_BASE - 40)		/*£¨Data authentication SM2 authentication signature error£©*/
#define SECERR_9F69								(SECERR_BASE - 41)		/*£¨Data authentication 9F69 error£©*/
#define SECERR_FDDA_VER							(SECERR_BASE - 42)		/*£¨Data authentication FDDA version error£©*/
#define SECERR_ATC								(SECERR_BASE - 43)		/*£¨Data authentication ATC error£©*/
#define SECERR_INALCMD_ERRDATA					(SECERR_BASE - 44)		/*£¨Data authentication forces data errors£©*/
#define SECERR_NOPAN							(SECERR_BASE - 45)		/*£¨Data authentication without tag 5A£©*/
#define SECERR_ICCDDLEN							(SECERR_BASE - 46)		/*£¨the length of ICC Dynamic Data is less£©*/
#define SECERR_RRP								(SECERR_BASE - 47)		/*(paypass CDA RRP not match)*/
#define SECERR_ERRHASH_INDICATOR 				(SECERR_BASE - 48)		/*(Hash indication of data authentication error)*/
#define SECERR_NOHASH_ALGORITHM					(SECERR_BASE - 49)		/*(Data authentication without hash algorithm)*/
#define SECERR_NO9F4BOR92						(SECERR_BASE - 50) 		/*(Data authentication without return tag9F4B or tag92)*/
#define SECERR_FAILDDA							(SECERR_BASE - 51) 		/*(Data authentication DDA failed)*/
#define SECERR_NOREMAINDER						(SECERR_BASE - 52) 		/*(Without 92 or 9F48)*/



//Cardholder Verification
#define CVERR_BASE								(EMV_ERR_BASE - 1700)	/*(Cardholder Certification benchmark Error Code)*/
#define CVERR_8ELENWRONG						(CVERR_BASE - 1) 	/*(tag8E length error)*/
#define CVERR_OFFLINEPIN						(CVERR_BASE - 2)		/*(Input offline pin failed)*/
#define CVERR_ONLINEPIN							(CVERR_BASE - 3)		/*(Input online pin failed)*/
#define CVERR_AIPCVM_NOSUPP						(CVERR_BASE - 4)		/*(Card AIP does not support CVM)*/
#define CVERR_NO8E								(CVERR_BASE - 5)		/*(Without CVM list)*/
#define CVERR_NOCVMRULES						(CVERR_BASE - 6)		/*(Without CVM rules)*/


//Terminal Action Analysis and Card Action Analysis
#define GACERR_BASE								(EMV_ERR_BASE - 1800)		/*(Terminal and card behavior analysis benchmark error code)*/
#define GACERR_WRONGREQ							(GACERR_BASE - 1)  	 /*(GAC response error)*/
#define GACERR_CDOLPACKET						(GACERR_BASE - 2)		/*(GAC CDOL data pack error)*/
#define GACERR_GACCMD							(GACERR_BASE - 3)		/*(GAC return code error)*/
#define GACERR_TLVDEOCDE						(GACERR_BASE - 4)		/*(GAC returns data parsing error)*/
#define GACERR_CDAREQUIRE77						(GACERR_BASE - 5)	/*Tag 80 templet cda requested and AAC not returned*/
#define GACERR_TAG80VALLEN						(GACERR_BASE - 6)	/*(GAC Returns Tag Length Error)*/
#define GACERR_77NO9F27							(GACERR_BASE - 7)		/*£¨GAC returns 77 templates without returning 9F27£©*/
#define GACERR_77NO9F36							(GACERR_BASE - 8)		/*£¨GAC returns 77 templates without returning 9F36£©*/
#define GACERR_77NO9F4B							(GACERR_BASE - 9)		/*£¨GAC returns 77 templates without returning 9F4B£©*/
#define GACERR_NOT77OR80						(GACERR_BASE - 10)		/*£¨GAC returns not 77 or 80 templates£©*/
#define GACERR_AARRET							(GACERR_BASE - 11)		/*(CID request AAR returned by GAC)*/
#define GACERR_WRONGCID							(GACERR_BASE - 12)		/*(GAC returns the wrong CID)*/
#define GACERR_77NO9F26							(GACERR_BASE - 13)		/*£¨GAC returns 77 templates without returning 9F26£©*/
#define GACERR_NO9F10							(GACERR_BASE - 14)		/*(GAC without return 9F10)*/
#define GACERR_SPECIAL_PAD0						(GACERR_BASE - 15)		/*(GAC returns zero data)*/
#define GACERR_DRDOLPACKET						(GACERR_BASE - 16)		/*(DRDOL Data Pack Failed)*/
#define GACERR_RAC_SW12_NO9000					(GACERR_BASE - 17)		/*(RAC response code is not 9000)*/
#define GACERR_80_9F36_EXIST					(GACERR_BASE - 18)		/*(GAC returns 80 templates with 9F36)*/
#define GACERR_ECMAC							(GACERR_BASE - 20)		/*(GAC Error in Electronic Cash)*/
#define GACERR_GETECBALANCE						(GACERR_BASE - 21)		/*(Failed to obtain EC balance)*/
#define GACERR_EC_BALANCELACK					(GACERR_BASE - 22)        /*(EC Balance not enough)*/
#define GACERR_ECONLY_GOONLINE					(GACERR_BASE - 23)        /*(Pure electronic cash card requires EC online but refuse)*/
#define GACERR_EC_THRESHOLD						(GACERR_BASE - 24)        /*(EC balance < transaction amount + threshold, resulting in online)*/
#define GACERR_RETURN_81						(GACERR_BASE - 25)		/*£¨GAC returns 77 templates without returning 81£©*/
#define GACERR_RETURN_9F01						(GACERR_BASE - 26)		/*£¨GAC returns 77 templates without returning 9F01£©*/
#define GACERR_RETURN_ERROR						(GACERR_BASE - 27)		/*(GAC returns CID length error)*/
#define GACERR_9F10ERROR						(GACERR_BASE - 28)        /*(GAC returns 9F10 format error)*/
#define GACERR_77_GACNOC5						(GACERR_BASE - 29)		/*£¨GAC returns 77 templates without returning tag C5£©*/
#define GACERR_77_ALLNOC5						(GACERR_BASE - 30)		/*(Neither MCCS GPO nor GAC returned to tag C5)*/
#define GACERR_DISTINCTCVMINFOR					(GACERR_BASE - 31)		/*(MCCS GPO and GAC return tage C5 CVM different ways)*/
#define GACERR_ECHO								(GACERR_BASE - 32)		/*(MCCS GAC apdu without return, need to enter torn transaction)*/
#define GACERR_77NO9F4BHAVE9F26					(GACERR_BASE - 33)		/*(MCCS GAC without 9F4B but has 9F26)*/

#define GACERR_CDOL1_PACK					(GACERR_BASE -34)		/*(First GAC CDOL package failed)*/
#define GACERR_FMT_ERROR					(GACERR_BASE -35)		/*(The 77 template format returned by JCB GAC is wrong.)*/
#define GACERR_TAG_DUP						(GACERR_BASE -36)		/*(JCB failed to store the label returned by GAC)*/
#define GACERR_CID_MISSING					(GACERR_BASE -37)		/*(JCB storage GAC without return CID)*/
#define GACERR_ATC_MISSING					(GACERR_BASE -38)		/*(JCB GAC without return ATC)*/
#define GACERR_9F4B_MISSING					(GACERR_BASE -39)		/*(JCB GAC without return 9F4B)*/
#define GACERR_AC_MISSING					(GACERR_BASE -40)		/*(JCB GAC without return AC)*/
#define GACERR_9F50_MISSING					(GACERR_BASE -41)		/*(JCB GAC without return 9F50)*/
#define GACERR_ACTYPE_ERR					(GACERR_BASE -42)		/*(JCB GAC return AC type wrong)*/
#define GACERR_AAC							(GACERR_BASE -43)		/*(JCB GAC return AAC)*/
#define GACERR_9F5F_INVALID					(GACERR_BASE -44)		/*(JCB GAC returns 9F5F format error)*/
#define GACERR_9F60_INVALID					(GACERR_BASE -45)		/*(JCB GAC returns 9F60 format error)*/

#define CVM_NOT_SUPPORT						(GACERR_BASE -46)		/*(JCB does not support CVM authentication)*/
#define GACERR_LEGACY_FMT					(GACERR_BASE -47)		/*(JCB LEGACY mode GAC returns not 80 template)*/
#define GACERR_LEGACY_DENIAL				(GACERR_BASE -48)		/*(JCB LEGACY mode GAC returned CID is not ARQC)*/
#define GACERR_QUIT							(GACERR_BASE -49)		/*(GAC cancels the transaction)*/
#define GACERR_9F27LENWRONG					(GACERR_BASE -50)		/*(9F27 length error)*/
#define GACERR_9F36LENWRONG					(GACERR_BASE -51)		/*(9F36 length error)*/
#define GACERR_9F26LENWRONG					(GACERR_BASE -52)		/*(9F26 length error)*/

#define GMDERR_BASE							(GACERR_BASE - 60)		/*(JCB GMD command benchmark Error Code)*/
#define GMDERR_MDOL_PACK					(GMDERR_BASE - 1)		/*(JCB GMD failed to package MDOL)*/
#define GMDERR_FMT_ERROR					(GMDERR_BASE - 2)		/*(JCB GMD command returns data error)*/
#define GMDERR_TK2ED_MISSING				(GMDERR_BASE - 3)		/*(JCB GMD command without return tag57)*/
#define GMDERR_MS_DENIAL					(GMDERR_BASE - 4)		/*(JCB MS mode GMD return code is 6300)*/

//echo command
#define EHCO_BASE							(GACERR_BASE - 70)		/*(echo command benchmark Error Code)*/
#define EHCO_FMT_ERROR						(EHCO_BASE - 1)		/*(JCB echo command returns data format error)*/
#define EHCO_TAG_DUP						(EHCO_BASE - 2)		/*(JCB echo returns data with duplicate tag)*/
#define EHCO_CID_MISSING					(EHCO_BASE - 3)		/*(JCB echo without return CID)*/
#define EHCO_ATC_MISSING					(EHCO_BASE - 4)		/*(JCB echo without return ATC)*/
#define EHCO_9F4B_MISSING					(EHCO_BASE - 5)		/*(JCB echo without return 9F4B)*/
#define EHCO_AC_MISSING						(EHCO_BASE - 6)		/*(JCB echo without return AC)*/
#define EHCO_9F50_MISSING					(EHCO_BASE - 7)		/*(JCB echo without return 9F50)*/
#define EHCO_QUIT							(EHCO_BASE - 8)		/*(Echo command cancel transaction*/

//Script Processing and Completion
#define COMERR_BASE								(EMV_ERR_BASE - 1900)		/*(Basic and EMV complete benchmark error code)*/
#define COMERR_BASE_SCRIPT						(COMERR_BASE - 1)		/*(Script exceeds limit)*/
#define COMERR_BASE_SCRIPTEMPTY					(COMERR_BASE - 2)           /*(EC script empty)*/
#define COMERR_BASE_ECLOADAMOUNT				(COMERR_BASE - 3)           /*(EC deposit amount exceeds the limit)*/
#define COMERR_BASE_SCRIPTRET					(COMERR_BASE - 4)           /*(Script execution error)*/
#define COMERR_BASE_SCRIPTERROR					(COMERR_BASE - 5)           /*(Script error)*/


//Flash Card
#define FLASHCARD_ERR_BASE						(EMV_ERR_BASE - 2000)		/*(Qpboc flash card benchmark error code)*/
#define FLASHCARD_ERR_PAN_NO_EQUAL				(FLASHCARD_ERR_BASE - 1)	/*(Flash card number does not match)*/
#define FLASHCARD_ERR_ATC_NO_EQUAL				(FLASHCARD_ERR_BASE - 2)	/*(Flash card application transaction counter does not match)*/
#define FLASHCARD_ERR_CURCODE_NO_EQUAL			(FLASHCARD_ERR_BASE - 3)	/*(Flash card currency code does not match)*/
#define FLASHCARD_ERR_BALANCE_NO_EQUAL			(FLASHCARD_ERR_BASE - 4)	/*(Flash card electronic cash balance does not match)*/
#define FLASHCARD_ERR_NO_PURCHASE				(FLASHCARD_ERR_BASE - 5)	/*(Flash card without occor transaction)*/
#define FLASHCARD_ERR_PURCHASE_GPO				(FLASHCARD_ERR_BASE - 6)	/*(Flash card GPO error)*/
#define FLASHCARD_ERR_LAST_RECORD_NO_RESPONSE	(FLASHCARD_ERR_BASE - 7)	/*(The last read record of the flash card is not responding)*/
#define FLASHCARD_ERR_NO_GET_PAN				(FLASHCARD_ERR_BASE - 8)	/*(Flash card can not get the card number)*/
#define FLASHCARD_ERR_PAN_NOT_SAME				(FLASHCARD_ERR_BASE - 9)	/*(Flash card number is different)*/
#define FLASHCARD_ERR_AID_NOT_SAME				(FLASHCARD_ERR_BASE - 10)	/*(Flash card aid is not the same)*/
#define FLASHCARD_ERR_PAN_NO_RESPONSE			(FLASHCARD_ERR_BASE - 11)	/*(The last record of the flash card is not responding.)*/


//Preprocess & qPboc errorcode
#define RFERR_BASE								(EMV_ERR_BASE - 2100)		/*(Preprocessing benchmark error code)*/
#define RFERR_PREPROCESS_PARAFILE				(RFERR_BASE - 1)         /**< (Preprocessing parameter file error) */
#define RFERR_PREPROCESS_AMTQUIT				(RFERR_BASE - 2)         /**< (Preprocessing input amount user exits) */
#define RFERR_PREPROCESS_AMTTIMEOUT				(RFERR_BASE - 3)         /**< (Preprocessing input amount timeout) */
#define RFERR_PREPROCESS_AMTFAIL				(RFERR_BASE - 4)         /**< (Preprocessing input amount failed) */
#define RFERR_PREPROCESS_AMTLIMITOVER			(RFERR_BASE - 5)         /**< (Preprocessed input amount exceeds the limit) */
#define RFERR_PREPROCESS_REQONLINE				(RFERR_BASE - 6)         /**< (Preprocessing requires online, terminal cannot be online) */
#define RFERR_PREPROCESS_NOAID					(RFERR_BASE - 7)         /**< (AID is 0) */
#define RFERR_ICCDEACTIVE						(RFERR_BASE - 11)        /**< (RF card removal failed) */
#define RFERR_ICCRETURNERROR					(RFERR_BASE - 12)        /**< (Card returned error) */
#define RFERR_READAPPDATA						(RFERR_BASE - 13)        /**< (Failed to read application data) */
#define RFERR_BLKCARD							(RFERR_BASE - 14)        /**< (Card blacklist) */
#define RFERR_ICCNOEFFECT						(RFERR_BASE - 15)        /**< (The card is not valid) */
#define RFERR_ICCEXPIRE							(RFERR_BASE - 16)        /**< (Card has invalid) */
#define RFERR_DATAAUTH							(RFERR_BASE - 17)        /**< (Card data authentication failed) */
#define RFERR_TRACK2EDATA						(RFERR_BASE - 18)        /**< (Card second magnetic equivalent data failed) */
#define RFERR_ICCFCHECK							(RFERR_BASE - 19)        /**< (Frequency check exceeds limit) */
#define RFERR_ECPURE_CANNOT_ONLINE				(RFERR_BASE - 20)        /**< £¨Pure electronic cash card can not be online£© */
#define RFERR_CARD_DENIAL						(RFERR_BASE - 21)        /**< £¨Card rejection£© */
#define RFERR_NOODA								(RFERR_BASE - 22)        /**< £¨Card AIP has no data authentication£© */
#define RFERR_9F10CID							(RFERR_BASE - 23)        /**<  (Card 9F10 return transaction result error)*/
#define RFERR_DATAAUTHNOPAN						(RFERR_BASE - 24)        /**< (Card data authentication without card number information)*/
#define RFERR_FDDAFAIL_SUPPBOC					(RFERR_BASE - 25)        /**< (FDDA failed, card and terminal support contact PBOC)*/
#define RFERR_ODAFAIL_DENIAL					(RFERR_BASE - 26)        /**< (ODA failed, terminal refused to trade)*/



//Paypass
#define PPERR_BASE								(EMV_ERR_BASE - 2200)	/*(Paypass benchmark error code)*/
#define PPERR_TRANS_LIMITOVER					(PPERR_BASE - 1)		/*£¨transaction usAmount over all Terminal Contactless Transaction Limit£© */
#define PPERR_TRACK								(PPERR_BASE - 2)		/*£¨paypass error track data£©*/
#define PPERR_TRACK_PCVC3						(PPERR_BASE - 3)  	 /*(paypass pcvc3)*/
#define PPERR_TRACK_PUNATC						(PPERR_BASE - 4)		/*paypass punatc*/
#define PPERR_TRACK_NATC						(PPERR_BASE - 5)		/*paypass natc*/
#define PPERR_TRACK_KLTT						(PPERR_BASE - 6) 		/*k_track < t_track*/
#define PPERR_TRACK_NUN							(PPERR_BASE - 7)		/*£¨track data wrong unpredictable number£©*/
#define PPERR_TRACK1_PAN						(PPERR_BASE - 8) 		/*£¨not the same as track2£©*/
#define PPERR_TRACK1_EXPIREDATE					(PPERR_BASE - 9)   	/*£¨not the same as track2£©*/
#define PPERR_CCC_CMD							(PPERR_BASE - 10) 	/*£¨compute cryptographic checksum error£© */
#define PPERR_CCC_RESPONSE						(PPERR_BASE - 11) 	/*£¨compute cryptographic checksum response error£© */
#define PPERR_CCC_UDOL_NO9F6A					(PPERR_BASE - 12)   	/*£¨UDOL NO 9F6A£© */
#define PPERR_CCC_TRACK1CVC3					(PPERR_BASE - 13)     	/*£¨CCC command return track2 cvc3 error£© */
#define PPERR_CCC_TRACK2CVC3					(PPERR_BASE - 14)   	/*£¨CCC command return track1 cvc3 error£© */
#define PPERR_CCC_ATC							(PPERR_BASE - 15)   	 /*£¨CCC command return atc error£© */
#define PPERR_CCC_NODEF_UDOL					(PPERR_BASE - 16)   	/*£¨NO default UDOL £©*/
#define PPERR_CCC_DOLPACKET						(PPERR_BASE - 17)	 /*£¨UDOL pack error£© */
#define PPERR_PP_ENTERPIN						(PPERR_BASE - 18)    	 /*£¨input pin error£©  */
#define PPERR_READAPPDATA						(PPERR_BASE - 19)    	/* £¨read app  error£©  */
#define PPERR_PREPROCESS_AMTQUIT				(PPERR_BASE - 20)    /*£¨Preprocessing input amount user exits£©*/
#define PPERR_PREPROCESS_AMTTIMEOUT				(PPERR_BASE - 21)    /*£¨Preprocessing input amount timeout£©*/
#define PPERR_PREPROCESS_AMTFAIL				(PPERR_BASE - 22)    /*£¨Preprocessing input amount failed£©*/
#define PPERR_SDAFAIL							(PPERR_BASE - 23)    /*£¨SDA failed transaction terminate£©*/
#define PPERR_TRACK1_LEN						(PPERR_BASE - 24)    /*£¨Track1 length error£©*/
#define PPERR_TRACK2_LEN						(PPERR_BASE - 25)    /*£¨Track2 length error£©*/
#define PPERR_SAVEDATA							(PPERR_BASE - 26)    /*£¨save data error£©*/
#define PPERR_READPARAM							(PPERR_BASE - 27)    /*£¨read param config error£© */
#define PPERR_CCC_PCII							(PPERR_BASE - 28)    /*£¨CCC command return PCII error£© */
#define PPERR_CCC_TRACK_DATA_MISSING			(PPERR_BASE - 29)    /*£¨Used to specifically indicate data loss errors£©*/




//Paywave
#define PWERR_BASE								(EMV_ERR_BASE - 2300)	/*(Paywave benchmark error code)*/
#define PWERR_9F26								(PWERR_BASE - 5)		/*(error or no 9F26 in read record command)*/
#define PWERR_9F36								(PWERR_BASE - 6)		/*(error or no 9F36 in read record command)*/
#define PWERR_9F10								(PWERR_BASE - 7)		/*(error or no 9F10 in read record command*/
#define PWERR_57								(PWERR_BASE - 8)		/*(error or no 57 in read record command)*/
#define PWERR_9F27								(PWERR_BASE - 9)		/*(error or no 9F27 in read record command)*/
#define PWERR_INTER_TRANS						(PWERR_BASE - 10)        /* (international transaction)*/
#define PWERR_NO9F74							(PWERR_BASE - 11)        /* (Did not return 9F74) */
#define PWERR_PW_ENTERPIN						(PWERR_BASE - 18)        /*(define request online error)*/
#define PWERR_AIP_NOSUPPORTFDDA				(PWERR_BASE - 19)        /*(aip no support fdda)*/

//Ruoay
#define RUERR_BASE								(EMV_ERR_BASE - 2400)	/*(Rupay benchmark error code)*/
#define RUERR_SERVICE_KCV_WRONG				(RUERR_BASE - 1)	/*(Rupay KCV error)*/

//MIR
#define MIR_BASE								(EMV_ERR_BASE - 2500)	/*(MIR benchmark error code)*/
#define MIR_PROTOCOLWRONG						(MIR_BASE - 1)			/*(MIR protocol value error)*/
#define MIR_AIP_NOSUPPORT_EMV					(MIR_BASE - 2)			/*(MIR aip does not support EMV mode)*/
#define MIR_NOHAVE_DF6F						(MIR_BASE - 3)			/*(MIR Protocol 2 does not have DF6F ODOL)*/
#define MIR_DF6F_WRONG						(MIR_BASE - 4)			/*(MIR Protocol 2 DF6F ODOL error)*/
#define MIR_SERVICE_NOTALLOW					(MIR_BASE - 5)			/*£¨MIR protocol 2 service not allowed£©*/
#define MIR_SDADMISSING					(MIR_BASE - 6)				/*£¨MIR without SDAD£©*/
#define MIR_TRANSPERFORM_BADSW					(MIR_BASE - 7)				/*£¨MIR executes transaction commands without returning 9000£©*/
#define MIR_TRANSPERFORM_NOT77						(MIR_BASE - 8)		/*£¨MIR executes transaction commands without returning 77 templates£©*/
#define MIR_TRANSPERFORM_77NOT9F27						(MIR_BASE - 9)		/*£¨MIR executes transaction commands without returning 9F27)*/
#define MIR_TRANSPERFORM_77NOT9F36						(MIR_BASE - 10)		/*£¨MIR executes transaction commands without returning 9F36)*/
#define MIR_TRANSPERFORM_77NOT9F71						(MIR_BASE - 11)		/*£¨MIR executes transaction commands without returning 9F71)*/
#define MIR_TRANSCPMPLETE_BADSW						(MIR_BASE - 12)		/*£¨MIR executes transaction commands without returning 9000)*/
#define MIR_TRANSCPMPLETE_NO9F26						(MIR_BASE - 13)		/*£¨MIR executes transaction commands without returning 9F26)*/
#define MIR_TRANSPERFORM_NORECOVRY						(MIR_BASE - 14)		/*(MIR does not support recovery when executing transaction commands)*/
#define MIR_TRANSPERFORM_LIMIT						(MIR_BASE - 15)		/*(MIR Executes Transaction Command Restoration Over Restriction)*/
#define MIR_TRANSCOMPLETE_NORECOVRY						(MIR_BASE - 16)		/*(MIR Executes Transaction Completion Command does not support recovery)*/
#define MIR_TRANSCOMPLETE_LIMIT						(MIR_BASE - 17)		/*(MIR Executes Transaction Completion Command Restore Over Restriction)*/
#define MIR_TRANSREADRECORD_NORECOVRY						(MIR_BASE - 18)		/*£¨MIR Read Record Command does not support recovery£©*/
#define MIR_TRANSREADRECORD_LIMIT						(MIR_BASE - 19)		/*£¨MIR Read Record Command Restore Over Restriction£©*/


/**< CAPK Oper Errorcode */
#define   CAPKERR_BASE							(-4000)		/*(Public key file operation benchmark error code)*/
#define   CAPKERR_FILEOPEN						(CAPKERR_BASE - 1) 		/**<  (File open error)*/
#define   CAPKERR_FILEWRITE						(CAPKERR_BASE - 2) 		/**<  (write file error)*/
#define   CAPKERR_FILEREAD						(CAPKERR_BASE - 3) 		/**<  (Read file error)*/
#define   CAPKERR_CHKSUM						(CAPKERR_BASE - 4) 		/**<  (Public key checksum error)*/
#define   CAPKERR_LOST							(CAPKERR_BASE - 5) 		/**< (This public key was not found) */
#define   CAPKERR_PARAM							(CAPKERR_BASE - 6) 		/**<  (Parameter error)*/
#define   CAPKERR_FILELEN						(CAPKERR_BASE - 7) 		/**< (File length error) */

/**< Revocation/Exception list Oper Errorcode */
#define   LIST_BASE							(-4100)		/*(Public key collection list and card blacklist file operation benchmark error code)*/
#define   LIST_FILEOPEN						(LIST_BASE - 1) 		/**<  (File open error)*/
#define   LIST_FILEWRITE					(LIST_BASE - 2) 		/**<  (write file error)*/
#define   LIST_FILEREAD						(LIST_BASE - 3) 		/**<  (Read file error)*/
#define   LIST_LEN_EXCEED					(LIST_BASE - 4) 		/**< (Data length is exceeds limit) */
#define   LIST_RECORD_NOFOUND				(LIST_BASE - 5) 		/**< (Without find the corresponding record) */
#define   LIST_PARAM						(LIST_BASE - 6) 		/**< (Parameter error) */
#define   LIST_FILELEN					    (LIST_BASE - 7) 		/**< file length error */


/**< AID Oper Errorcode*/
#define	AIDERR_BASE								(EMV_ERR_BASE - 5000)	/*(AID parameter file operation benchmark error code)*/
#define	AIDERR_FILEOPEN							(AIDERR_BASE - 1) 	/**< (File open error) */
#define	AIDERR_FILEWRITE						(AIDERR_BASE - 2) 	/**<  (write file error)*/
#define	AIDERR_FILEREAD							(AIDERR_BASE - 3) 	/**<  (Read file error)*/
#define	AIDERR_CHKSUM							(AIDERR_BASE - 4) 	/**< (Public key checksum error) */
#define	AIDERR_LOST								(AIDERR_BASE - 5) 	/**< (This AID was not found) */
#define	AIDERR_PARAM							(AIDERR_BASE - 6) 	/**< (Parameter error) */
#define	AIDERR_FILELEN							(AIDERR_BASE - 7) 	/**< (File length error) */
#define	AIDERR_UPTAID							(AIDERR_BASE - 8) 	/**< (Synchronous update of AID corresponding data fails when updating terminal configuration parameters) */
#define	AIDERR_PARSE							(AIDERR_BASE - 9) 	/**< (AID parsing failed)*/
#define AIDERR_DISABLE							(AIDERR_BASE - 10)  /**< (AID is not available)*/


#endif

