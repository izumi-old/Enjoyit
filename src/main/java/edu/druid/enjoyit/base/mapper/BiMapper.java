package edu.druid.enjoyit.base.mapper;

public interface BiMapper<T, Q> {
    Q map1(T t);
    T map2(Q q);
}
