package com.vvautotest.utils;

import android.content.Context;
import android.widget.Spinner;

import com.vvautotest.R;
import com.vvautotest.model.MenuData;
import com.vvautotest.model.SpinnerData;

import java.util.ArrayList;

public class DummyData {

    public static ArrayList<SpinnerData> loadCategory() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new SpinnerData(i + 1, "Category " + (i + 1)));
        }
        return list;
    }
    public static ArrayList<SpinnerData> loadOpeningStockCategory() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"Store Bulker (MT)"));
        list.add(new SpinnerData(2,"Spreader (MT)"));

        return list;
    }
    public static ArrayList<SpinnerData> loadFundType() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"Fund for Store Bulker"));
        list.add(new SpinnerData(2,"Fund for Spreader"));

        return list;
    }

    public static ArrayList<SpinnerData> loadMDStockCategory() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"Store Bulker"));
        list.add(new SpinnerData(2,"Spreader"));

        return list;
    }

    public static ArrayList<SpinnerData> loadLeaveType() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"Leave Type 1"));
        list.add(new SpinnerData(2,"Leave Type 2"));
        list.add(new SpinnerData(3,"Leave Type 3"));
        list.add(new SpinnerData(4,"Leave Type 4"));
        return list;
    }

    public static ArrayList<SpinnerData> loadLeaveCategory() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"Leave Category 1"));
        list.add(new SpinnerData(2,"Leave Category 2"));
        list.add(new SpinnerData(3,"Leave Category 3"));
        list.add(new SpinnerData(4,"Leave Category 4"));

        return list;
    }

    public static ArrayList<SpinnerData> loadMSNAMECategory() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        list.add(new SpinnerData(1,"KA-01AA"));
        list.add(new SpinnerData(2,"MP66"));
        list.add(new SpinnerData(3,"MH-3145"));

        return list;
    }

    public static ArrayList<SpinnerData> loadSite() {
        ArrayList<SpinnerData> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new SpinnerData(i + 1, "Site " + (i + 1)));
        }
        return list;
    }

    public static ArrayList<MenuData> loadMenu(Context context) {
        ArrayList<MenuData> list = new ArrayList<>();
        /*list.add(new MenuData(AppUtils.Menu.HOME, context.getResources().getDrawable(R.drawable.home),
                AppUtils.AppRoute.ROUTE_HOME, ""));
        list.add(new MenuData(AppUtils.Menu.PHOTOS, context.getResources().getDrawable(R.drawable.image_gallery),
                AppUtils.AppRoute.ROUTE_PHOTOS, ""));
        list.add(new MenuData(AppUtils.Menu.DPR_DWR_PDF, context.getResources().getDrawable(R.drawable.pdf_file),
                AppUtils.AppRoute.ROUTE_DPR_DWR_PDF, ""));
        list.add(new MenuData(AppUtils.Menu.MANPOWER_ENTRY, context.getResources().getDrawable(R.drawable.manpower),
                AppUtils.AppRoute.ROUTE_MANPOWER_ENTRY, AppUtils.SubMenu.SUB_MANPOWER_ENTRY));
        list.add(new MenuData(AppUtils.Menu.PROGRESS_ENTRY, context.getResources().getDrawable(R.drawable.process),
                AppUtils.AppRoute.ROUTE_PROGRESS_ENTRY, ""));
        list.add(new MenuData(AppUtils.Menu.DIRECT_MATERIAL, context.getResources().getDrawable(R.drawable.material),
                AppUtils.AppRoute.ROUTE_DIRECT_MATERIAL, AppUtils.SubMenu.SUB_DIRECT_MATERIAL));
        list.add(new MenuData(AppUtils.Menu.MAIN_DPR, context.getResources().getDrawable(R.drawable.daily_progress),
                AppUtils.AppRoute.ROUTE_MAIN_DPR, ""));
        list.add(new MenuData(AppUtils.Menu.OPENING_STOCK, context.getResources().getDrawable(R.drawable.open_stock),
                AppUtils.AppRoute.ROUTE_OPENING_STOCK, ""));
        list.add(new MenuData(AppUtils.Menu.RECEIPT_STOCK, context.getResources().getDrawable(R.drawable.receipt_stock),
                AppUtils.AppRoute.ROUTE_RECEIPT_STOCK, ""));
        list.add(new MenuData(AppUtils.Menu.CLOSING_STOCK, context.getResources().getDrawable(R.drawable.closing_stock),
                AppUtils.AppRoute.ROUTE_CLOSING_STOCK, ""));
        list.add(new MenuData(AppUtils.Menu.MW_AND_HSD_ISSUE, context.getResources().getDrawable(R.drawable.machine_issue),
                AppUtils.AppRoute.ROUTE_MW_AND_HSD_ISSUE, AppUtils.SubMenu.SUB_MW_AND_HSD_ISSUE));
        list.add(new MenuData(AppUtils.Menu.LEAVE_APPLICATION, context.getResources().getDrawable(R.drawable.leave_application),
                AppUtils.AppRoute.ROUTE_LEAVE_APPLICATION, ""));
        list.add(new MenuData(AppUtils.Menu.FUND_REQUEST, context.getResources().getDrawable(R.drawable.fund_request),
                AppUtils.AppRoute.ROUTE_FUND_REQUEST, ""));
        list.add(new MenuData(AppUtils.Menu.PRE_SALE, context.getResources().getDrawable(R.drawable.site_surway),
                AppUtils.AppRoute.ROUTE_PRE_SALE, ""));
        list.add(new MenuData(AppUtils.Menu.LOGOUT, context.getResources().getDrawable(R.drawable.logout),
                AppUtils.AppRoute.ROUTE_LOGOUT, ""));*/
        return list;
    }

    public static ArrayList<MenuData> loadHomeMenu(Context context) {
        ArrayList<MenuData> list = new ArrayList<>();
     /*   list.add(new MenuData(AppUtils.Menu.ADD_PHOTOS, context.getResources().getDrawable(R.drawable.image_gallery), AppUtils.AppRoute.ROUTE_ADD_PHOTOS, ""));
        list.add(new MenuData(AppUtils.Menu.ADD_PDF, context.getResources().getDrawable(R.drawable.pdf_file), AppUtils.AppRoute.ROUTE_ADD_PDF, ""));
        list.add(new MenuData(AppUtils.Menu.ADD_PROGRESS, context.getResources().getDrawable(R.drawable.process), AppUtils.AppRoute.ROUTE_ADD_PROGRESS, ""));
        list.add(new MenuData(AppUtils.Menu.ADD_MANPOWER_ATTENDANCE, context.getResources().getDrawable(R.drawable.manpower), AppUtils.AppRoute.ROUTE_ADD_MANPOWER_ATTENDANCE, ""));
  */     return list;
    }
}
