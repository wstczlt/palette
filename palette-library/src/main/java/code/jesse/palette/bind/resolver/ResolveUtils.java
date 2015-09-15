package code.jesse.palette.bind.resolver;


import java.util.List;

import code.jesse.palette.tools.JavaCalls;

/**
 * @author zhulantian@gmail.com
 */
public class ResolveUtils {

    private ResolveUtils() {
        // utility class
    }

    public static Object resolveValue(Object model, String value) {
        String[] fields = value.split("\\.");
        Object parentObject = model;
        for (int i = 0; i < fields.length && parentObject != null; ++i) {
            String field = fields[i];
            String realField = field;
            int arrayIndex = -1;
            if (field.contains("[") && field.endsWith("]")) {
                realField = field.substring(0, field.indexOf('['));
                arrayIndex = Integer.parseInt(field.substring(field.indexOf('[') + 1, field.indexOf(']')));
            }

            Object fieldValue = JavaCalls.getField(parentObject, realField);
            if (arrayIndex != -1) { // 处理集合类型
                if (fieldValue instanceof List) {
                    parentObject = ((List) fieldValue).get(arrayIndex);
                } else if (fieldValue instanceof Object[]) {
                    parentObject = ((Object[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof int[]) {
                    parentObject = ((int[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof long[]) {
                    parentObject = ((long[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof short[]) {
                    parentObject = ((short[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof byte[]) {
                    parentObject = ((byte[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof float[]) {
                    parentObject = ((float[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof double[]) {
                    parentObject = ((double[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof boolean[]) {
                    parentObject = ((boolean[]) fieldValue)[arrayIndex];
                } else if (fieldValue instanceof char[]) {
                    parentObject = ((char[]) fieldValue)[arrayIndex];
                } else {
                    throw new IllegalArgumentException("Field is not a array type:" + value);
                }
            } else {
                parentObject = fieldValue;
            }

            if (i == fields.length - 1) { // the last
                return parentObject;
            }
        }
        return null;
    }
}