package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class NumberService {

    public boolean isBig(int number){
        return number > 50;
    }

}
