package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;

public class MxCellsWrapper <T> {

    public final JsonArray mxCells;
    public final T meta;

    public static final MxCellsWrapper<Integer> EMPTY = new MxCellsWrapper<>(JsonArray.EMPTY, 0);

    public MxCellsWrapper(JsonArray mxCells, T meta){
        this.mxCells = mxCells;
        this.meta = meta;
    }
}
