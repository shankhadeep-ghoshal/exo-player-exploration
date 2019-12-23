package org.shankhadeepghoshal.exoplayertutorial.utils;

public class TupleData<T,V> {
    private final T dataField1;
    private final V datafield2;

    public TupleData(T dataField1, V datafield2) {
        this.dataField1 = dataField1;
        this.datafield2 = datafield2;
    }

    public T getDataField1() {
        return dataField1;
    }

    public V getDatafield2() {
        return datafield2;
    }
}