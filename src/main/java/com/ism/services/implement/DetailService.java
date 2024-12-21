package com.ism.services.implement;

import java.sql.SQLException;
import java.util.List;
import java.util.Collections;

import com.ism.data.entities.Detail;
import com.ism.data.repository.IDetailRepository;
import com.ism.services.IDetailService;

public class DetailService implements IDetailService {
    private IDetailRepository detailRepository;

    public DetailService(IDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    @Override
    public boolean add(Detail value) {
        try {
            return detailRepository.insert(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Detail> findAll() {
        try {
            return detailRepository.selectAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public int length() {
        return detailRepository.size();
    }
}
