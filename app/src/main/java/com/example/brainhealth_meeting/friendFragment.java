package com.example.brainhealth_meeting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.brainhealth_meeting.SimpleProfileContent;
import com.example.brainhealth_meeting.SimpleProfileContent.SimpleProfileItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class friendFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public friendFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static friendFragment newInstance(int columnCount) {
        friendFragment fragment = new friendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DownloadPhotoAsyncTask task = new DownloadPhotoAsyncTask(new DownloadPhotoAsyncTask.AsyncCallback() {
                public void preExecute() {
                }

                public void postExecute(JSONObject result) {
                    if (result == null) {
                        showLoadError();
                        return;
                    }
                    int user_id = 0;
                    String user_name = "default";
                    int user_age = 0;
                    String image_base64 = "image_base64";
                    byte[] decoded_image;
                    Bitmap bitmap = null;
                    ArrayList<SimpleProfileItem> list = new ArrayList<>();
                    try {
                        JSONArray array = result.getJSONArray("data");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            user_id = obj.getInt("id");
                            user_name = obj.getString("name");
                            user_age = obj.getInt("age");
                            image_base64 = obj.getString("image64");
                            decoded_image = Base64.decode(image_base64, Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decoded_image, 0, decoded_image.length);
                            list.add(new SimpleProfileItem(user_id, user_name, user_age, bitmap));
                        }

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView.setAdapter(new MyfriendRecyclerViewAdapter(list, mListener));
                }

                public void progressUpdate(int progress) {
                }

                public void cancel() {
                }
            });
            task.execute("http://brainhealthmeeting.tk:8000/test/booking", "GET");
            recyclerView.setAdapter(new MyfriendRecyclerViewAdapter(SimpleProfileContent.ITEMS, mListener));
        }
        return view;
    }

    private void showLoadError() {
        Toast toast = Toast.makeText(getActivity(), "failed to obtain data", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SimpleProfileItem item);
    }
}
