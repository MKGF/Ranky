package com.desierto.Ranky.domain.converter;

import com.desierto.Ranky.domain.entity.ValueEnum;
import java.util.Arrays;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ValueEnumConverter<T extends Enum<T> & ValueEnum<E>, E> implements
    AttributeConverter<T, E> {

  private final Class<T> clazz;

  public ValueEnumConverter(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public E convertToDatabaseColumn(T attribute) {
    return attribute != null ? attribute.getValue() : null;
  }

  @Override
  public T convertToEntityAttribute(E value) {
    T[] enums = clazz.getEnumConstants();

    for (T e : enums) {
      if (e.getValue().equals(value)) {
        return e;
      }
    }

    return Arrays.stream(enums).filter(e -> e.getValue().equals(value)).findFirst().orElseThrow();
  }
}
