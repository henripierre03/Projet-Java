package com.ism.data.repository.implement;

import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Detail;
import com.ism.data.repository.IDetailRepository;

public class DetailRepository extends Repository<Detail> implements IDetailRepository {
    public DetailRepository() {
        super(Detail.class);
    }
}
