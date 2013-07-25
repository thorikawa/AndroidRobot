package com.polysfactory.contactphoto.entity;

/**
 * コンタクトPeopleエンティティ
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class People implements Comparable<People> {

    /** ID */
    public String id;

    /** 名前 */
    public String displayName;

    /** ふりがな */
    public String phoneticName;

    @Override
    public boolean equals(Object o) {
        return equals((People) o);
    }

    public boolean equals(People o) {
        if (o == null) {
            return false;
        } else {
            return id.equals(o.id);
        }
    }

    @Override
    public int compareTo(People another) {
        return displayName.compareTo(another.displayName);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (id != null) {
            hash += id.hashCode();
        }
        return hash;
    }
}
