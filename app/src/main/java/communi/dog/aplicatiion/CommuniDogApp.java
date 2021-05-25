package communi.dog.aplicatiion;

import android.app.Application;

public class CommuniDogApp extends Application {
    private static CommuniDogApp instance = null;
    private DB localDdb;
    private MapState mapState;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // todo: load from db
        this.mapState = new MapState();
        localDdb = new DB(); // todo: need context? maybe for sp?
    }

    public static CommuniDogApp getInstance() {
        return instance;
    }

    public DB getDb() {
        return localDdb;
    }

    public MapState getMapState() {
        return mapState;
    }
}
