package com.dominik;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class LifeBitService {
    @Autowired
    private LifeBitRepository lifeBitRepository;

    public List<LifeBit> getAllInfo()
    {
        List<LifeBit> lifeBits = new ArrayList<>();
        lifeBitRepository.findAll().forEach(lifeBits::add);
        return lifeBits;
    }
    public LifeBit getInfoById(long id)
    {
        return lifeBitRepository.findById(id).get();
    }
    public String getError(int status)
    {
        Optional<List<LifeBit>> res = lifeBitRepository.findByStatus(status);
        if (!res.get().isEmpty())
            return "error";
        return "The system is working fine";

    }
    public void postLifeBit(LifeBit lifeBit)
    {
        lifeBitRepository.save(lifeBit);
    }
    public void DeleteAllInfo()
    {
        lifeBitRepository.deleteAll();
    }


}
