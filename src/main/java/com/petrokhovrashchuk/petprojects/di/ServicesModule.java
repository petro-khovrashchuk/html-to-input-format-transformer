package com.petrokhovrashchuk.petprojects.di;

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

}
