package com.newland.sdk.mtype;

/**
 * Device module<p>
 * 
 *
 *
 */
public interface Module {
	
	/**
	 * 是否是标准模块<P>
	 *  Is it a standard module<P>
	 * 
	 * @return
	 */
	public boolean isStandardModule();
	
	/**
	 * If the module is a standard module defined in sdk, get the enumeration type corresponding the standard module <p>
	 * 
	 * @return The module type, if it is a non-standard module type, the return will be null. 
	 */
	public ModuleType getStandardModuleType();
	
	/**
	 *  If the module is a stated external extended module, get the extended module type <p>
	 * 
	 * @return The module type, if it is a non-standard module type, the return will be null.
	 */
	public String getExModuleType();
	
	/**
	 *  Get the device object where this module is<p>
	 * 
	 * @return Device object where this module is
	 */
	public Device getOwner();
	
	/**
	 *  Get the description of this module<p>
	 * 	
	 * @return Module description 
	 */
	public String getDescription();

}
