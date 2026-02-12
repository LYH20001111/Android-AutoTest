package com.newland.iso.core;

import com.newland.iso.core.IFB_AMOUNT;
import com.newland.iso.core.IFB_BINARY;
import com.newland.iso.core.IFB_BITMAP;
import com.newland.iso.core.IFB_LLCHAR;
import com.newland.iso.core.IFB_LLLCHAR;
import com.newland.iso.core.IFB_LLLNUM;
import com.newland.iso.core.IFB_LLNUM;
import com.newland.iso.core.IFB_NUMERIC;
import com.newland.iso.core.IF_CHAR;
import com.newland.iso.core.ISOBasePackager;
import com.newland.iso.message.packager.IFieldPackager;

public class ISO93BPackager extends ISOBasePackager {
	 public final static ISO93BPackager INSTANCE = new ISO93BPackager();
	 private static final boolean pad = true; //BCD码统一左补0，右对齐到边界
	    protected IFieldPackager fld[] = {
	    /*000*/ new IFB_NUMERIC (  4, "Message Type Indicator", pad),
	    /*001*/ new IFB_BITMAP  ( 8, "Bitmap"),
	    /*002*/ new IFB_LLNUM   ( 19, "Primary Account number", pad),
	    /*003**/ new IFB_NUMERIC (  6, "Processing Code", pad),
	    /*004*/ new IFB_NUMERIC ( 12, "Amount, Transaction", pad),
	    /*005*/ new IFB_NUMERIC ( 12, "Amount, Settlement", pad),
	    /*006*/ new IFB_NUMERIC ( 12, "Amount, Cardholder billing", pad),
	    /*007*/ new IFB_NUMERIC ( 10, "Date and time, transmission", pad),
	    /*008*/ new IFB_NUMERIC (  8, "Amount, Cardholder billing fee", pad),
	    /*009*/ new IFB_NUMERIC (  8, "Conversion rate, Reconciliation", pad),
	    /*010*/ new IFB_NUMERIC (  2, "Conversion rate, Cardholder billing", pad),
	    /*011**/ new IFB_NUMERIC (  6, "Systems trace audit number", pad),
	    /*012*/ new IFB_NUMERIC (  6, "Date and time, Local transaction", pad),
	    /*013*/ new IFB_NUMERIC (  4, "Date, Effective", pad),
	    /*014*/ new IFB_NUMERIC (  4, "Date, Expiration", pad),
	    /*015*/ new IFB_NUMERIC (  4, "Date, Settlement", pad),
	    /*016*/ new IFB_NUMERIC (  4, "Date, Conversion", pad),
	    /*017*/ new IFB_NUMERIC (  4, "Date, Capture", pad),
	    /*018*/ new IFB_NUMERIC (  3, "Merchant type", pad),
	    /*019*/ new IFB_NUMERIC (  3, "Country code, Acquiring institution", pad),
	    /*020*/ new IF_CHAR (  3, "Country code, Primary account number"),
	    /*021*/ new IFB_NUMERIC (  3, "Country code, Forwarding institution", pad),
	    
	    /*022*/ new IFB_NUMERIC     (  3, "Point of service data code",pad),
	    /*023*/ new IFB_NUMERIC (  3, "Card sequence number", pad),
	    /*024**/ new IFB_NUMERIC (  3, "NETWORK INTERNATIONAL IDENTIFIEER",pad),
	    /*025*/ new IFB_NUMERIC (  2, "POINT OF SERVICE CONDITION CODE", pad),
	    
	    /*026*/ new IFB_NUMERIC (  2, "POINT OF SERVICE PIN CAPTURE COD", pad),
	    /*027*/ new IFB_NUMERIC (  2, "AUTHORIZATION IDENTIFICATION RESP LEN", pad),
	    
	    /*028*/ new IFB_NUMERIC (  28, "AMOUNT, TRANSACTION FEE", pad),
	    
	    /*029*/ new IFB_NUMERIC (  8, "AMOUNT, SETTLEMENT FEE", pad),
	    /*030*/ new IFB_NUMERIC (  8, "AMOUNT, TRANSACTION PROCESSING FEE", pad),
	    /*031*/ new IFB_NUMERIC  ( 8, "AMOUNT, SETTLEMENT PROCESSING FEE",pad),
	    /*032*/ new IFB_LLNUM   ( 11, "Acquirer institution ident code", pad),
	    /*033*/ new IFB_LLNUM   ( 11, "Forwarding institution ident code", pad),
	    
	    /*034*/ new IFB_LLCHAR  ( 28, "PAN EXTENDED"),
	    /*035*/ new IFB_LLNUM( 37, "Track 2 data",pad),
	    /*036*/ new IFB_LLLNUM (104, "Track 3 data",pad),
	    
	    
	    /*037*/ new IF_CHAR     ( 12, "Retrieval reference number"),
	    /*038*/ new IF_CHAR     (  6, "Approval code"),
	    /*039*/ new IF_CHAR (  2, "RESPONSE CODE"),
	    /*040*/ new IF_CHAR (  3, "SERVICE RESTRICTION CODE"),
	    /*041**/ new IF_CHAR     (  8, "Card acceptor terminal identification"),
	    /*042**/ new IF_CHAR    ( 15, "Card acceptor identification code"),
	    
	    /*043*/ new IFB_LLCHAR  ( 40, "Card acceptor name/location"),
	    /*044*/ new IFB_LLCHAR  ( 25, "Additional response data"),
	    /*045*/ new IFB_LLCHAR  ( 76, "Track 1 data"),
	    ///*046*/ new IFB_LLLCHAR (204, "Amounts, Fees"),
	    /*046*/ new IFB_LLLCHAR (999, "ADITIONAL DATA - ISO"),
	    /*047*/ new IFB_LLLCHAR (999, "ADITIONAL DATA - NATIONAL"),
	    /*048*/ new IFB_LLLCHAR  (322, "ADITIONAL DATA - PRIVATE"),
	    
	    /*049*/ new IFB_NUMERIC (  3, "Currency code, Transaction",pad),
	    /*050*/ new IF_CHAR     (  3, "CURRENCY CODE, SETTLEMENT"),
	    /*051*/ new IF_CHAR     (  3, "Currency code, Cardholder billing"),
	    /*052*/ new IFB_BINARY  (  8, "Personal identification number (PIN) data"),
	    
	    /*053*/ new IFB_NUMERIC( 16, "Security related control information",pad),
	    /*054*/ new IFB_LLLCHAR (999, "Amounts, additional"),
	    /*055*/ new IFB_LLLCHAR(255,"RESERVED ISO"),
	    /*056*/ new IFB_LLLCHAR (999, "RESERVED ISO"),
	    /*057*/ new IFB_LLLCHAR (999, "RESERVED NATIONAL"),
	    /*058*/ new IFB_LLLCHAR (255, "RESERVED NATIONAL"),
	    /*059*/ new IFB_LLLCHAR (999, "RESERVED NATIONAL"),
	    /*060*/ new IF_CHAR  (  13, "RESERVED PRIVATE"),
	    //TODO
	    /*061**/ new IFB_LLLCHAR (999, "RESERVED PRIVATE"),
	    /*062*/ new IFB_LLLCHAR (512, "RESERVED PRIVATE"),
	    /*063*/ new IFB_LLLCHAR (163, "RESERVED PRIVATE"),

	    
	    /*064*/ new IFB_BINARY  (  8, "Message authentication code field"),
	    /*065*/ new IFB_BINARY  (  8, "BITMAP, EXTENDED"),
	    
	    /*066*/ new IF_CHAR ( 1 , "SETTLEMENT CODE"),
	    /*067*/ new IFB_NUMERIC (  2, "Extended payment data", pad),
	    /*068*/ new IFB_NUMERIC (  3, "Country code, receiving institution", pad),
	    
	    /*069*/ new IFB_NUMERIC (  3, "Country code, settlement institution", pad),
	    /*070*/ new IFB_NUMERIC (  3, "Country code, authorizing agent Inst.", pad),
	    
	    /*071*/ new IFB_NUMERIC (  4, "Message number", pad),
	    /*072*/ new IFB_NUMERIC (  4, "MESSAGE NUMBER LAST", pad),
	    /*073*/ new IF_CHAR (6, "DATE ACTION"),
	    /*074*/ new IFB_NUMERIC ( 10, "Credits, number", pad),
	    /*075*/ new IFB_NUMERIC ( 10, "Credits, reversal number", pad),
	    /*076*/ new IFB_NUMERIC ( 10, "Debits, number", pad),
	    /*077*/ new IFB_NUMERIC ( 10, "Debits, reversal number", pad),
	    /*078*/ new IFB_NUMERIC ( 10, "Transfer, number", pad),
	    /*079*/ new IFB_NUMERIC ( 10, "Transfer, reversal number", pad),
	    /*080*/ new IFB_NUMERIC ( 10, "Inquiries, number", pad),
	    /*082*/ new IFB_LLNUM ( 12, "CREDITS, PROCESSING FEE AMOUNT", pad),
	    /*083*/ new IFB_LLNUM ( 12, "CREDITS, TRANSACTION FEE AMOUNT", pad),
	    /*084*/ new IFB_LLNUM ( 12, "DEBITS, PROCESSING FEE AMOUNT", pad),
	    /*085*/ new IFB_LLNUM ( 12, "DEBITS, TRANSACTION FEE AMOUNT", pad),
	    
	    /*086*/ new IFB_LLNUM ( 16, "Credits, amount", pad),
	    /*087*/ new IFB_LLNUM ( 16, "Credits, reversal amount", pad),
	    /*088*/ new IFB_LLNUM ( 16, "Debits, amount", pad),
	    /*089*/ new IFB_LLNUM ( 16, "Debits, reversal amount", pad),
	    
	    /*090*/ new IFB_LLNUM ( 42, "ORIGINAL DATA ELEMENTS", pad),
	    /*091*/ new IFB_NUMERIC (  1, "FILE UPDATE CODE", pad),
	    /*092*/ new IFB_NUMERIC (  2, "FILE SECURITY CODE", pad),
	    /*093*/ new IFB_NUMERIC   ( 5, "RESPONSE INDICATOR", pad),
	    /*094*/ new IFB_NUMERIC   ( 7, "SERVICE INDICATOR", pad),
	    /*095*/ new IFB_LLCHAR  ( 42, "REPLACEMENT AMOUNTS"),
	    /*096*/ new IFB_NUMERIC (  8,"MESSAGE SECURITY CODE",pad),
	    
	    /*097*/ new IFB_AMOUNT  (16,"Amount, Net reconciliation", pad),
	    /*098*/ new IFB_LLCHAR     ( 25, "Payee"),
	    /*099*/ new IFB_LLNUM  ( 11, "Settlement institution Id code",pad),
	    /*100*/ new IFB_LLNUM   ( 11, "Receiving institution Id code", pad),
	    /*101*/ new IFB_LLCHAR  ( 17, "File name"),
	    /*102*/ new IFB_LLCHAR  ( 28, "Account identification 1"),
	    /*103*/ new IFB_LLCHAR  ( 28, "Account identification 2"),
	    /*104*/ new IFB_LLLCHAR (100, "Transaction description"),
	    /*105*/ new IFB_LLLCHAR (999, "Reserved for ISO use"),
	    /*106*/ new IFB_LLLCHAR (999, "Reserved for ISO use"),
	    /*107*/ new IFB_LLLCHAR (999, "Reserved for ISO use"),
	    /*108*/ new IFB_LLLCHAR (999, "Reserved for ISO use"),
	    /*109*/ new IFB_LLLCHAR (999, "Reserved for ISO use"),
	    /*110*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*111*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*112*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*113*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*114*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*115*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*116*/ new IFB_LLLCHAR (999, "Reserved for national use"),
	    /*117*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*118*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*119*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*120*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*121*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*122*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*123*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*124*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*125*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*126*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*127*/ new IFB_LLLCHAR (999, "Reserved for private use"),
	    /*128*/ new IFB_BINARY  (  8, "Message authentication code field"),
	    ///*129*/ new IFB_LLLCHAR (50,  "Extends field")
	    };
	    public ISO93BPackager() {
	        super();
	        setFieldPackager(fld);
	    }
}
