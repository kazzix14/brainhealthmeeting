package com.example.brainhealth_meeting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Types;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    public static final int reg_choose_photo_code = 0;
    public static final int request_permission_code = 1212;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();
        Button select_photo_button = getActivity().findViewById(R.id.reg_select_photo_button);
        select_photo_button.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                                       intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                       intent.setType("image/*");
                                                       intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                           intent.putExtra(Intent.EXTRA_MIME_TYPES, Types.ARRAY);
                                                       }
                                                       if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                                               != PackageManager.PERMISSION_GRANTED) {
                                                           ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request_permission_code);
                                                       } else {
                                                           getActivity().startActivityForResult(Intent.createChooser(intent, null), reg_choose_photo_code);
                                                       }
                                                   }

                                               }

        );
        Button confirm_button = getActivity().findViewById(R.id.reg_confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {

            int id = -1;

            @Override
            public void onClick(View v) {
                DownloadPhotoAsyncTask task = new DownloadPhotoAsyncTask(new DownloadPhotoAsyncTask.AsyncCallback() {
                    public void preExecute() {
                    }

                    public void postExecute(JSONObject result) {
                        Toast toast = Toast.makeText(getActivity(), "Welcome!!", Toast.LENGTH_SHORT);
                        toast.show();
                        try {
                            id = result.getInt("id");
                            SharedPreferences prefs = getActivity().getSharedPreferences("savedata", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("my_user_id", id);
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void progressUpdate(int progress) {
                    }

                    public void cancel() {
                    }
                });

                TextView name = getActivity().findViewById(R.id.reg_edit_text_name);
                TextView age = getActivity().findViewById(R.id.reg_edit_text_age);
                TextView language = getActivity().findViewById(R.id.reg_edit_text_language);
                TextView location = getActivity().findViewById(R.id.reg_edit_text_location);
                TextView message = getActivity().findViewById(R.id.reg_edit_text_message);
                ImageView profile_photo = getActivity().findViewById(R.id.reg_profile_photo);

                Bitmap bitmap = ((BitmapDrawable) profile_photo.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                String image_base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                String url = "http://brainhealthmeeting.tk:8000/test/registration?name=" + name.getText() + "&age=" + age.getText() + "&language=" + language.getText() + "&location=" + location.getText() + "&message=" + message.getText() + "&image64=" + image_base64;
                task.execute(url, "POST");
            }

        });

    }

    private void showLoadError() {
        Toast toast = Toast.makeText(getActivity(), "failed to obtain data", Toast.LENGTH_SHORT);
        toast.show();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
