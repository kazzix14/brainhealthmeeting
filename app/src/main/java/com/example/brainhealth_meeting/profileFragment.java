package com.example.brainhealth_meeting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Logger;

import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link profileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "myid";

    // TODO: Rename and change types of parameters

    public int mId;

    private TextView mName;
    private TextView mAge;
    private TextView mLanguage;
    private TextView mLocation;
    private TextView mMessage;
    private ImageView mPhoto;

    private OnFragmentInteractionListener mListener;

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1) {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mId = getArguments().getInt(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mName = view.findViewById(R.id.profile_name);
        mAge = view.findViewById(R.id.profile_age);
        mLanguage = view.findViewById(R.id.profile_language);
        mLocation = view.findViewById(R.id.profile_location);
        mMessage = view.findViewById(R.id.profile_message);
        mPhoto = view.findViewById(R.id.profile_photo);
        DownloadPhotoAsyncTask task = new DownloadPhotoAsyncTask(new DownloadPhotoAsyncTask.AsyncCallback() {
            public void preExecute() {
            }

            public void postExecute(JSONObject result) {
                if (result == null) {
                    showLoadError();
                    return;
                }
                try {

                    mName.setText(result.getString("name"));
                    mAge.setText("age\n  " + result.getString("age"));
                    mLanguage.setText("language\n  " + result.getString("language"));
                    mLocation.setText("location\n  " + result.getString("location"));
                    mMessage.setText("message\n  " + result.getString("message"));

                    String image_base64 = "image_base64";
                    byte[] decoded_image;
                    Bitmap bitmap = null;

                    image_base64 = result.getString("image64");
                    decoded_image = Base64.decode(image_base64, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(decoded_image, 0, decoded_image.length);
                    mPhoto.setImageBitmap(bitmap);


                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }

            public void progressUpdate(int progress) {
            }

            public void cancel() {
            }
        });

        task.execute("http://brainhealthmeeting.tk:8000/test/profile?id=" + mId, "GET");
        return view;
    }

    private void showLoadError() {
        Toast toast = Toast.makeText(getActivity(), "failed to obtain data", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button add_button = getActivity().findViewById(R.id.add_to_friend_button);
        add_button.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              DownloadPhotoAsyncTask task = new DownloadPhotoAsyncTask(new DownloadPhotoAsyncTask.AsyncCallback() {
                                                  public void preExecute() {
                                                  }

                                                  public void postExecute(JSONObject result) {
                                                      Toast toast = Toast.makeText(getActivity(), "done", Toast.LENGTH_SHORT);
                                                      toast.show();
                                                  }

                                                  public void progressUpdate(int progress) {
                                                  }

                                                  public void cancel() {
                                                  }
                                              });
                                              SharedPreferences prefs = getActivity().getSharedPreferences("savedata", Context.MODE_PRIVATE);
                                              int id = prefs.getInt("my_user_id", -1);
                                              //SharedPreferences.Editor editor =prefs.edit();
                                              //editor.clear();
                                              //editor.commit();
                                              if (id == -1) {
                                                  Log.e("app", "user id does not exit");
                                              }

                                              task.execute("http://brainhealthmeeting.tk:8000/test/friend?id=" + id + "&friend=" + mId, "POST");
                                          }

                                      }

        );

        Button meeting_button = getActivity().findViewById(R.id.set_up_a_meeting_button);
        meeting_button.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  mListener.onFragmentInteraction(profileFragment.this);
                                              }
                                          }

        );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(profileFragment f);
    }
}
