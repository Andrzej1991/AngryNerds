package com.company.andrzej.rolki.projekttestowy.component;

import com.company.andrzej.rolki.projekttestowy.MainActivity;
import com.company.andrzej.rolki.projekttestowy.module.ServiceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Andrzej on 2017-06-26.
 */

@Singleton
//tutaj mamy nasz component, który wskazuje jakie mamy moduły,
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
    //wskazujemy gdzie chcemy go uzyc
    void inject(MainActivity activity);
}
