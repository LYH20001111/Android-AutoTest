package com.newland.sdk.me.module.emv.structure;

import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.module.common.emv.SimpleEmvPackager;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AbstractEmVPackage structure<p>
 * Mainly support for expansion pack structure <p>
 *
 * @since v1.0
 */
public abstract class AbstractEMVPackage {

    /**
     * Expansion pack, store non-analyzable data field<p>
     * Include failed analysis and non-attainable definition types <p>
     */
    private TLVPackage externalPackage = InnerUtils.newTlvPackage();

    /**
     * Unknown tags in object may be set by this method. The tags and values set by this method will participate in the final packing process. <p>
     * Its packing priority is higher than {@link EMVTagDefined}definition and will cover its setting
     *
     * @param tag   Tlv tags that are set
     * @param value Tlv values that are set
     */
    public void setExternal(int tag, byte[] value) {
        if (externalPackage.hasTag(tag)) {//If it exists, delete it.
            try {
                externalPackage.deleteByTag(tag);
            } catch (Exception e) {
            }
        }
        externalPackage.append(tag, value);
    }

    /**
     * Get to TLVPackage
     *
     * @param tags Tag list
     * @return
     */
    public TLVPackage setExternalInfoPackage(List<Integer> tags) {
        TLVPackage pckg = InnerUtils.newTlvPackage();
        for (Integer tag : tags) {
            byte[] value = null;
            try {
                value = externalPackage.getValue(tag);
                if (value != null)
                    pckg.append(tag, value);
            } catch (Exception e) {
                continue;
            }
        }
        return pckg;
    }

    /**
     * Get tlv tags already set<p>
     * This method is unable to get the data defined by {@link EMVTagDefined}and set, and can only get the data defined by {@link #setExternal(int, byte[])}<p>
     * After unpacking is completed, this method can get all tag data returned by all devices, including the data of failed unpacking defined by {@link EMVTagDefined}.
     *
     * @param tag
     * @return
     */
    public byte[] getExternal(int tag) {
        return externalPackage.getValue(tag);
    }

    /**
     * Remove tlv tags
     *
     * @param tag Tag
     */
    public void removeExternal(int tag) {
        externalPackage.deleteByTag(tag);
    }

    public TLVPackage getExternalPackage() {
        return externalPackage;
    }

    public Set<Integer> getRelativeTags() {
        return getRelativeTags(getClass());
    }

    public static Set<Integer> getRelativeTags(Class<? extends AbstractEMVPackage> entity) {
        Field[] fields = entity.getDeclaredFields();
        Set<Integer> tags = new HashSet<Integer>();
        for (Field field : fields) {
            EMVTagDefined defined = field.getAnnotation(EMVTagDefined.class);
            if (defined != null)
                tags.add(defined.tag());
        }
        return tags;
    }
}
