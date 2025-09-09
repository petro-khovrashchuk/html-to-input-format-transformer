package com.petrokhovrashchuk.petprojects.di;

import com.petrokhovrashchuk.petprojects.di.annotation.JsoupParser;
import com.petrokhovrashchuk.petprojects.service.JsoupParseService;
import dagger.Module;
import dagger.Provides;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

@Module
public class ServicesModule {

  @Provides
  public Connection provideConnection() {
    return Jsoup.newSession();
  }

  @Provides
  @JsoupParser
  public JsoupParseService provideJsoupParseService() {
    return new JsoupParseService();
  }
}
