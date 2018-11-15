package com.util.library.widget.addresspicker.tool;

import java.util.List;

/**
 * @description:
 * @author: whb
 * @date: 2018/11/14 18:57
 */
public class ListUtil {

    public ListUtil() {
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }
}
