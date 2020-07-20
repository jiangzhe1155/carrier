package org.jz.admin.ddd;

/**
 * @author jz
 * @date 2020/07/20
 */
public abstract class Convertor<T, V> {

    public abstract V serialize(T domainObject);

    public abstract T deserialize(V domainObject);
}
