package com.vvautotest.db;

import android.content.Context;
import android.os.AsyncTask;

import com.vvautotest.model.Category;

import java.util.List;

public class CategoryRepo {
    private CategoryDao categoryDao;

    public CategoryRepo(Context context)
    {
        AppDatabase CategoryDatabase = AppDatabase.getDatabase(context);
        categoryDao = CategoryDatabase.categoryDao();
    }

    public List<Category> getAllCategory(){
        return categoryDao.getCategoryList();
    }

    public void insertNewCategory(Category Category)
    {
        new InsertNewCategoryAsyncTask(categoryDao).execute(Category);
    }

    public boolean isCategoryExist(int id)
    {
        return categoryDao.isCategoryExist(id);
    }

    public void deleteCategoryRecord(Category Category){
        new DeleteCategoryAsyncTask(categoryDao).execute(Category);
    }

    // we are creating a async task method to insert new record.
    private static class InsertNewCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao dao;

        private InsertNewCategoryAsyncTask(CategoryDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Category... model) {
            // below line is use to insert our modal in dao.
            dao.insertCategory(model[0]);
            return null;
        }
    }
    // we are creating a async task method to delete new course.
    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao dao;

        private DeleteCategoryAsyncTask(CategoryDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Category... model) {
            // below line is use to delete our modal
            dao.deleteCategory(model[0]);
            return null;
        }
    }
}
