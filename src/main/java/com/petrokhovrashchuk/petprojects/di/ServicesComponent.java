package com.petrokhovrashchuk.petprojects.di;

import com.petrokhovrashchuk.petprojects.service.GeeksForGeeksPagesTransformerImpl;
import com.petrokhovrashchuk.petprojects.service.JsoupParseService;
import com.petrokhovrashchuk.petprojects.service.PageLoaderImpl;
import dagger.Component;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = ServicesModule.class)
public interface ServicesComponent {

  PageLoaderImpl buildPageLoader();

  GeeksForGeeksPagesTransformerImpl buildGeeksFroGeeksPagesTransformer();

  JsoupParseService getJsoupParseService();
}
