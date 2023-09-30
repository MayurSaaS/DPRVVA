package com.vvautotest.db;

import android.content.Context;
import android.os.AsyncTask;

import com.vvautotest.model.UserItem;

import java.util.List;

public class UserItemRepo {

    private UserItemDao UserItemDao;

    public UserItemRepo(Context context)
    {
        AppDatabase UserItemDatabase = AppDatabase.getDatabase(context);
        UserItemDao = UserItemDatabase.userItemDao();
    }

    public List<UserItem> getAllUserItem(){
        return UserItemDao.getUsersList();
    }

    public void insertNewUserItem(UserItem UserItem)
    {
        new UserItemRepo.InsertNewUserItemAsyncTask(UserItemDao).execute(UserItem);
    }

    public boolean isUserItemExist(int id)
    {
        return UserItemDao.isUserExist(id);
    }

    public void deleteUserItemRecord(UserItem UserItem){
        new UserItemRepo.DeleteUserItemAsyncTask(UserItemDao).execute(UserItem);
    }

    // we are creating a async task method to insert new record.
    private static class InsertNewUserItemAsyncTask extends AsyncTask<UserItem, Void, Void> {
        private UserItemDao dao;

        private InsertNewUserItemAsyncTask(UserItemDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(UserItem... model) {
            // below line is use to insert our modal in dao.
            dao.insertUser(model[0]);
            return null;
        }
    }
    // we are creating a async task method to delete new course.
    private static class DeleteUserItemAsyncTask extends AsyncTask<UserItem, Void, Void> {
        private UserItemDao dao;

        private DeleteUserItemAsyncTask(UserItemDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(UserItem... model) {
            // below line is use to delete our modal
            dao.deleteUser(model[0]);
            return null;
        }
    }
}