package edu.kozhinov.enjoyit.protocol.mapper;

public interface BiMapper<T, Q> {
    Q map1(T t);
    T map2(Q q);
}
