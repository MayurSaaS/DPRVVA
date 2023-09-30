package com.vvautotest.fragments;

import android.app.ProgressDialog;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.model.Action;
import com.vvautotest.model.Site;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {

    ActionApiListener actionApiListener;

    public BaseFragment()
    {

    }
    public ArrayList<Action> getAction(){
        ArrayList<Action> list = null;
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", 1);
            jsonObject.put("siteID", 1);
            jsonObject.put("menuID", 1);
        }catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        AndroidNetworking.post(ServerConfig.Action_Details_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Sites")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        progressDialog.dismiss();
                                        L.printInfo(response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                        List<Action>  list = om.readValue(response.toString(), new TypeReference<List<Action>>(){});
                                        actionApiListener.onActionResponse(list);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        L.printError(anError.toString());
                                        progressDialog.dismiss();
                                    }
                                }
                );
        return list;
    }

    public void setActionApiListener(ActionApiListener actionApiListener)
    {
        this.actionApiListener = actionApiListener;
    }

    public interface ActionApiListener{
        void onActionResponse(List<Action> actionList);
    }

}
