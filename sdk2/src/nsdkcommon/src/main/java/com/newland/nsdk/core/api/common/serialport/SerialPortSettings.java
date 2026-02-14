package com.newland.nsdk.core.api.common.serialport;

/**
 * Serial port settings used when opening a serial port.
 */
public class SerialPortSettings {
    private BaudRate baudRate = BaudRate.BPS115200;
    private DataBits dataBits = DataBits.DATA_BIT_8;
    private ParityBit parityBit = ParityBit.NO_CHECK;
    private StopBits stopBits = StopBits.STOP_BIT_ONE;
    private boolean isBlocked = true;
    private boolean isFlowCtr = false;

    /**
     * Initializes a new instance of serial port settings using the specified baud rate, parity bit, data bits, and stop bits.
     *
     * @param baudRate  Baud rate, see {@link BaudRate}
     * @param dataBits  Data bits, see {@link DataBits}
     * @param parityBit Parity bit, see {@link ParityBit}
     * @param stopBits  Stop bits, see {@link StopBits}
     */
    public SerialPortSettings(BaudRate baudRate, DataBits dataBits, ParityBit parityBit, StopBits stopBits) {
       this.baudRate = baudRate;
       this.dataBits = dataBits;
       this.parityBit = parityBit;
       this.stopBits = stopBits;
       this.isBlocked = true;
       this.isFlowCtr = false;
    }

    public SerialPortSettings(BaudRate baudRate, DataBits dataBits, ParityBit parityBit, StopBits stopBits, boolean isBlocked) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.parityBit = parityBit;
        this.stopBits = stopBits;
        this.isBlocked = isBlocked;
        this.isFlowCtr = false;
    }

    public SerialPortSettings(BaudRate baudRate, DataBits dataBits, ParityBit parityBit, StopBits stopBits, boolean isBlocked, boolean isFlowCtr) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.parityBit = parityBit;
        this.stopBits = stopBits;
        this.isBlocked = isBlocked;
        this.isFlowCtr = isFlowCtr;
    }

    /**
     * Gets baud rate.
     *
     * @return Baud rate, see {@link BaudRate}
     */
    public BaudRate getBaudRate() {
        return baudRate;
    }

    /**
     * Sets baud rate.
     *
     * @param baudRate Baud rate, see {@link BaudRate}
     */
    public void setBaudRate(BaudRate baudRate) {
        this.baudRate = baudRate;
    }

    /**
     * Gets data bits
     *
     * @return Data bits, see {@link DataBits}
     */
    public DataBits getDataBits() {
        return dataBits;
    }

    /**
     * Sets data bits.
     *
     * @param dataBits Data bits, see {@link DataBits}
     */
    public void setDataBits(DataBits dataBits) {
        this.dataBits = dataBits;
    }

    /**
     * Gets parity bit.
     *
     * @return Parity bit, see {@link ParityBit}
     */
    public ParityBit getParityBit() {
        return parityBit;
    }

    /**
     * Sets parity bit.
     *
     * @param parityBit Parity bit, see {@link ParityBit}
     */
    public void setParityBit(ParityBit parityBit) {
        this.parityBit = parityBit;
    }

    /**
     * Gets stop bits.
     *
     * @return Stop bits, see {@link StopBits}
     */
    public StopBits getStopBits() {
        return stopBits;
    }

    /**
     * Sets stop bits.
     *
     * @param stopBits Stop bits, see {@link StopBits}
     */
    public void setStopBits(StopBits stopBits) {
        this.stopBits = stopBits;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setFlowCtr(boolean flowCtr) {
        isFlowCtr = flowCtr;
    }

    public boolean isFlowCtr() {
        return isFlowCtr;
    }
}
