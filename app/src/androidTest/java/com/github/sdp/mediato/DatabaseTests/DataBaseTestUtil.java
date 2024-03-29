package com.github.sdp.mediato.DatabaseTests;

import com.github.sdp.mediato.data.CollectionsDatabase;
import com.github.sdp.mediato.data.LocationDatabase;
import com.github.sdp.mediato.data.ReviewInteractionDatabase;
import com.github.sdp.mediato.data.UserDatabase;

public class DataBaseTestUtil {


  /**
   * Util function to make the database use the emulator
   */
  public static void useEmulator() {
    UserDatabase.database.useEmulator("10.0.2.2", 9000);
    CollectionsDatabase.database.useEmulator("10.0.2.2", 9000);
    LocationDatabase.database.useEmulator("10.0.2.2", 9000);
    ReviewInteractionDatabase.database.useEmulator("10.0.2.2", 9000);
  }

  /**
   * Clean the databases
   */
  public static void cleanDatabase() {
    UserDatabase.database.getReference().setValue(null);
    CollectionsDatabase.database.getReference().setValue(null);
    LocationDatabase.database.getReference().setValue(null);
    ReviewInteractionDatabase.database.getReference().setValue(null);
  }

}
