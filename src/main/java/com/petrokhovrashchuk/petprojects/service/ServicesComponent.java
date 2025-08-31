package com.petrokhovrashchuk.petprojects.service;

import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = ServicesModule.class)
public interface ServicesComponent {

  PageLoaderImpl buildPageLoader();

}
