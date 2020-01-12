package org.shankhadeepghoshal.exoplayertutorial.utils;

public class TupleData<T,V> {
    private final T dataField1;
    private final V dataField2;

    public TupleData(T dataField1, V dataField2) {
        this.dataField1 = dataField1;
        this.dataField2 = dataField2;
    }

    public T getDataField1() {
        return dataField1;
    }

    public V getDataField2() {
        return dataField2;
    }
}