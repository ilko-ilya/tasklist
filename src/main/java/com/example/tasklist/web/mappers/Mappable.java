package com.example.tasklist.web.mappers;

import java.util.List;

public interface Mappable<E, D> {
    D toDto(E entity);

    List<D> toListDto(List<E> entityList);

    E toEntity(D dto);

    List<E> toListEntity(List<D> dtoList);
}
