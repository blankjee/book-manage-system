package com.code.bms.common;

import lombok.Data;

import java.util.List;

@Data
public class DataGridDataSource<T> {

    private int total;

    private List<T> rows;

}
