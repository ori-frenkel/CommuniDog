package communi.dog.aplicatiion;

import android.app.Application;

public class CommuniDogApp extends Application {
    private static CommuniDogApp instance = null;
    private DB localDdb;
   // private MapState mapState; //todo: In the db

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        localDdb = new DB(); // todo: need context? maybe for sp?
      //  this.mapState = localDdb.getMapState(); //todo: In the db
    }

    public static CommuniDogApp getInstance() {
        return instance;
    }

    public DB getDb() {
        return localDdb;
    }

    public MapState getMapState() {
        return localDdb.getMapState();
    }
}
