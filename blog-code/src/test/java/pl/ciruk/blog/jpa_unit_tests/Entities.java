package pl.ciruk.blog.jpa_unit_tests;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.OneToOne;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.math.LongMath;

public class Entities {
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private static final long FIRST_OF_JANUARY_2014_IN_MILIS = 1388530800000l;
	
	private static final long MILIS_PER_YEAR = 365l * 24l * 60l * 60l * 1000l;
	
	@SuppressWarnings("restriction")
	private static final sun.misc.Unsafe UNSAFE = getUnsafe();
	
	private static final Map<Class<?>, Field[]> FIELDS_FOR_CLASS = Maps.newHashMap();

	public static <E> List<E> mockListOf(Class<E> entityClass, int expectedSize) {
		Preconditions.checkNotNull(entityClass);
		Preconditions.checkArgument(expectedSize > 0, "Positive number of instances is required");
		
		List<E> mockEntities = new ArrayList<>(expectedSize * 12 / 10);
		
		for (int i = 0; i < expectedSize; i++) {
			mockEntities.add(
					mock(entityClass)
			);
		}
		
		return mockEntities;
	}
	
	public static <E> E mock(Class<E> entityClass) {
		Preconditions.checkNotNull(entityClass);
		
		E entity = instantiateWithUnsafe(entityClass);
		
		Field[] fields = null;
		if (FIELDS_FOR_CLASS.containsKey(entityClass)) {
			fields = FIELDS_FOR_CLASS.get(entityClass);  
		} else {
			fields = entityClass.getDeclaredFields();
			FIELDS_FOR_CLASS.put(entityClass, fields);
		}
		
		try {
			for (Field field : fields) {
				if (field.isAnnotationPresent(Column.class)) {
					Object value = getValueForType(field.getType(), field.getAnnotation(Column.class));
					field.setAccessible(true);
					field.set(entity, value);
				} else if (field.isAnnotationPresent(OneToOne.class)) {
					Object value = mock(field.getType());
					field.setAccessible(true);
					field.set(entity, value);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException("Error occurred while setting fields' values", e);
		}
		
		return entity;
	}
	
	private static Object getValueForType(Class<?> type, Column columnDeclaration) {
		Object value = null;
		if (type.equals(Integer.class)) {
			value = RANDOM.nextInt();
		} else if (type.equals(Long.class)) {
			value = RANDOM.nextLong();
		} else if (type.equals(Double.class)) {
			value = RANDOM.nextDouble();
		} else if (type.equals(String.class)) {
			int length = columnDeclaration.length() > 0 ? columnDeclaration.length() : 10;
			value = randomString(length);
		} else if (type.equals(Timestamp.class)) {
			Date date = (Date) getValueForType(Date.class, columnDeclaration);
			value = new Timestamp(date.getTime());
		} else if (type.equals(java.sql.Date.class)) {
			Date date = (Date) getValueForType(Date.class, columnDeclaration);
			value = new java.sql.Date(date.getTime());
		} else if (Date.class.isAssignableFrom(type)) {
			long offset = LongMath.mod(RANDOM.nextLong(), MILIS_PER_YEAR);
			value = new Date(FIRST_OF_JANUARY_2014_IN_MILIS + offset);
		}
		return value;
	}
	
	static int bitsIn(long pattern, int len, int pos) {
		long b = pattern & (-1 >>> (64 - pos));
		b >>>= pos - len;

		return Long.valueOf(b).intValue();
		
	}
	
	static String randomString(int length) {
		String result = null;
		if (result == null) {
			char[] chars = new char[length];
			
			int bitsPerChar = 5;
			int charsPerLong = 64 / bitsPerChar;
			
			for (int i = 0; i < (length / charsPerLong) + 1; i++) {
				long bitPattern = RANDOM.nextLong();
				
				int startWithIndex  = i * charsPerLong;
				for (int j = 0; j < charsPerLong && startWithIndex+j < length; j++) {
					int offset = bitsIn(bitPattern, bitsPerChar, j)  % ('z'-'a');
					chars[startWithIndex + j] = (char) ('a' + offset);
				}
			}
			result = new String(chars);
		}
		
		return result;
	}
	
	private static <E> E instantiateWithUnsafe(Class<E> classToBeInstantiated) {
	    try {
	        E instance = (E) UNSAFE.allocateInstance(classToBeInstantiated);
	        return instance;
	    } catch (InstantiationException | SecurityException | IllegalArgumentException  e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	 
	private static sun.misc.Unsafe getUnsafe() {
	    Field theUnsafe = null;
		try {
			theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
		    sun.misc.Unsafe unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
		    return unsafe;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	    
	    return null;
	}
}
