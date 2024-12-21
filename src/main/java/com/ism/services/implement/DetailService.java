package com.ism.services.implement;

import java.util.List;

import com.ism.data.entities.Detail;
import com.ism.data.repository.IDetailRepository;
import com.ism.services.IDetailService;

public class DetailService implements IDetailService {
    private IDetailRepository detailRepository;

    public DetailService(IDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    @Override
    public Detail add(Detail value) {
        return detailRepository.insert(value);
    }

    @Override
    public List<Detail> findAll() {
        return detailRepository.selectAll();
    }

    @Override
    public int length() {
        return detailRepository.size();
    }
}
