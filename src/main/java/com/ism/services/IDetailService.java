package com.ism.services;

import java.util.List;

import com.ism.data.entities.Detail;

public interface IDetailService {
    Detail add(Detail value);
    List<Detail> findAll();
    int length();
}
