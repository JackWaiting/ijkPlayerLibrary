package com.platomix.lib.playerengine.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

public class ReflectUtil {
	
	private static Object findObjInR(String packageName, String className) {
		try {
			Class<?> managerClass = Class.forName(packageName + ".R");
			Class<?>[] classes = managerClass.getClasses();
			for (Class<?> c : classes) {
				int i = c.getModifiers();
				String name = c.getName();
				String s = Modifier.toString(i);
				if (s.contains("static") && name.endsWith("$" + className)) {
					return c.getConstructor().newInstance();
				} else {
					continue;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 获取R文件的内部类
	 * @param packageName 应用包名
	 * @param className 内部类名称，如id,layout,string等
	 * @return 获取R文件的内部类
	 */
	public static Class<?> findClassInR(String packageName, String className){
		Object obj = findObjInR(packageName, className);
		if(obj != null){
			return obj.getClass();
		}
		return null;
	}
	
	/**
	 * 反射出classR资源下的值
	 * @param context 上下文
	 * @param classR classR资源
     * @param filedName 文件名
	 * @return 反射出classR资源下的值
	 */
	public static int getIntInClassR(Context context, Class<?> classR, String filedName){
		if(classR != null){
			try {
				Field intField = classR.getField(filedName);
				int sourceId = intField.getInt(intField);
				return sourceId;
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (NotFoundException e){
			}
		}
		return -1;
	}
}
