package com.ibm.users.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class BeanUtilsCopy {

  /**
   * Ignore NULL filed during copy
   *
   * @param source
   * @param target
   */
  public static void copyPropertiesNoNull(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullField(source));
  }

  /**
   * @param obj
   * @return ignoreProperties
   */
  private static String[] getNullField(Object obj) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
    PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
    Set<String> nullFieldSet = new HashSet<>();
    if (propertyDescriptors.length > 0) {
      for (PropertyDescriptor p : propertyDescriptors) {
        String name = p.getName();
        Object value = beanWrapper.getPropertyValue(name);
        if (Objects.isNull(value)) {
          nullFieldSet.add(name);
        }
        if ("id".equalsIgnoreCase(name) && "0".equalsIgnoreCase(value.toString())) {
          nullFieldSet.add(name);
        }
      }
    }
    String[] nullField = new String[nullFieldSet.size()];
    return nullFieldSet.toArray(nullField);
  }

}
