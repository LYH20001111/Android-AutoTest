package com.newland.iso.core;

import java.util.BitSet;

import com.newland.iso.message.Field;
import com.newland.iso.message.FieldMap;
import com.newland.iso.message.MessageException;

public class ISOMsg extends FieldMap 
{
    protected int direction;
    public static final int INCOMING = 1;
    public static final int OUTGOING = 2;
    private static final long serialVersionUID = 4306251831901413975L;

    /**
     * Creates an ISOMsg
     */
    public ISOMsg () {
        super();
        direction = 0;
    }
    
    /**
     * Creates an ISOMsg with given mti
     * @param mti Msg's MTI
     */
    public ISOMsg (String mti) {
        this();
        try {
            setMTI (mti);
        } catch (MessageException e) {
            // should never happen
        }
    }
    
    /**
     * Sets the direction information related to this message
     * @param direction can be either ISOMsg.INCOMING or ISOMsg.OUTGOING
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * @return the direction (ISOMsg.INCOMING or ISOMsg.OUTGOING)
     * @see ISOChannel
     */
    public int getDirection() {
        return direction;
    }
    
    /**
     * @return true if this message is an incoming message
     * @see ISOChannel
     */
    public boolean isIncoming() {
        return direction == INCOMING;
    }
    /**
     * @return true if this message is an outgoing message
     * @see ISOChannel
     */
    public boolean isOutgoing() {
        return direction == OUTGOING;
    }
    
     /**
     * setup BitMap
     * @exception MessageException
     */
    public void recalcBitMap () throws MessageException {
        if (!dirty)
            return;

        BitSet bmap = new BitSet (((getMaxField()+62)>>6)<<6);
        for (int i=1; i<=maxField; i++)
            if (getField(i) != null) 
                bmap.set (i);
        set (new Field<BitSet> (-1, bmap));
        dirty = false;
    }
 
    /**
     * @param mti new MTI
     * @exception MessageException if message is inner message
     */
    public void setMTI (String mti) throws MessageException {
        if (isInner())
            throw new MessageException ("can't setMTI on inner message");
        set (new Field<String>(0, mti));
    }
    
    
    /**
     * @return current MTI
     * @exception MessageException on inner message or MTI not set
     */
    public String getMTI() throws MessageException {
        if (isInner())
            throw new MessageException ("can't getMTI on inner message");
        else if (!hasField(0))
            throw new MessageException ("MTI not available");
        return (String) getValue(0);
    }
    
    /**
     * @return true if message "seems to be" a request
     * @exception MessageException on MTI not set
     */
    public boolean isRequest() throws MessageException {
        return Character.getNumericValue(getMTI().charAt (2))%2 == 0;
    }
    
    /**
     * @return true if message "seems not to be" a request
     * @exception MessageException on MTI not set
     */
    public boolean isResponse() throws MessageException {
        return !isRequest();
    }
    /**
     * @return true if message is Retransmission
     * @exception MessageException on MTI not set
     */
    public boolean isRetransmission() throws MessageException {
        return getMTI().charAt(3) == '1';
    }
    /**
     * sets an appropiate response MTI.
     *
     * i.e. 0100 becomes 0110<br>
     * i.e. 0201 becomes 0210<br>
     * i.e. 1201 becomes 1210<br>
     * @exception MessageException on MTI not set or it is not a request
     */
    public void setResponseMTI() throws MessageException {
        if (!isRequest())
            throw new MessageException ("not a request - can't set response MTI");

        String mti = getMTI();
        char c1 = mti.charAt(3);
        char c2 = '0';
        switch (c1)
        {
            case '0' :
            case '1' : c2='0';break;
            case '2' :
            case '3' : c2='2';break;
            case '4' :
            case '5' : c2='4';break;

        }
        set (new Field<String> (0,
            mti.substring(0,2)
            +(Character.getNumericValue(getMTI().charAt (2))+1) + c2
            )
        );
    }
    
    /**
     * sets an appropiate retransmission MTI<br>
     * @exception MessageException on MTI not set or it is not a request
     */
    public void setRetransmissionMTI() throws MessageException {
        if (!isRequest())
            throw new MessageException ("not a request");

        set (new Field<String> (0, getMTI().substring(0,3) + "1"));
    }
}
