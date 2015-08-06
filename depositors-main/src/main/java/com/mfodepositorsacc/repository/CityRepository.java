package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.City;
import com.mfodepositorsacc.dmodel.Region;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 07.05.15.
 */
@Transactional(readOnly = true)
public interface CityRepository extends CrudRepository<City, Long> {

    public List<City> findByRegion(Region region);

}
