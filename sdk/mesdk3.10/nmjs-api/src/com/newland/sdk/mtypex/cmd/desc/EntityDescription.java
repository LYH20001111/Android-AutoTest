package com.newland.sdk.mtypex.cmd.desc;

import java.util.List;

/**
 * Entity description rule
 * @author chenliang
 *
 */
public abstract class EntityDescription {

	private final List<FieldDescription> fieldDescs;

	public EntityDescription(List<FieldDescription> fieldDescs){
		this.fieldDescs = fieldDescs;
	}

	public List<FieldDescription> getFieldDescs() {
		return fieldDescs;
	}
	
	
}
