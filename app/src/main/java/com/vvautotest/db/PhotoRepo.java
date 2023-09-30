package com.vvautotest.db;

import android.content.Context;
import android.os.AsyncTask;

import com.vvautotest.model.OfflinePhotoModel;
import java.util.List;

public class PhotoRepo {
    private PhotoDao photoDao;

    public PhotoRepo(Context context)
    {
        AppDatabase UserItemDatabase = AppDatabase.getDatabase(context);
        photoDao = UserItemDatabase.photoDao();
    }

    public List<OfflinePhotoModel> getAllUserItem(){
        return photoDao.getPhotosList();
    }

    public void insertNewPhotoItem(OfflinePhotoModel UserItem)
    {
        new InsertNewPhotoItemAsyncTask(photoDao).execute(UserItem);
    }

    public boolean isUserItemExist(int id)
    {
        return photoDao.isPhotoExist(id);
    }

    public void deletePhotoItemRecord(OfflinePhotoModel UserItem){
        new DeletePhotoItemAsyncTask(photoDao).execute(UserItem);
    }

    // we are creating a async task method to insert new record.
    private static class InsertNewPhotoItemAsyncTask extends AsyncTask<OfflinePhotoModel, Void, Void> {
        private PhotoDao dao;

        private InsertNewPhotoItemAsyncTask(PhotoDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(OfflinePhotoModel... model) {
            // below line is use to insert our modal in dao.
            dao.insertPhoto(model[0]);
            return null;
        }
    }
    // we are creating a async task method to delete new course.
    private static class DeletePhotoItemAsyncTask extends AsyncTask<OfflinePhotoModel, Void, Void> {
        private PhotoDao dao;

        private DeletePhotoItemAsyncTask(PhotoDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(OfflinePhotoModel... model) {
            // below line is use to delete our modal
            dao.deletePhoto(model[0]);
            return null;
        }
    }
}
